package com.farmacia.api.service;

import com.farmacia.api.exception.business.BusinessException;
import com.farmacia.api.mapper.VendaMapper;
import com.farmacia.api.model.Cliente;
import com.farmacia.api.model.ItemVenda;
import com.farmacia.api.model.Medicamento;
import com.farmacia.api.model.Venda;
import com.farmacia.api.model.enums.TipoMovimentacao;
import com.farmacia.api.repository.VendaRepository;
import com.farmacia.api.web.estoque.dto.MovimentacaoRequestDTO;
import com.farmacia.api.web.venda.dto.ItemVendaRequestDTO;
import com.farmacia.api.web.venda.dto.VendaRequestDTO;
import com.farmacia.api.web.venda.dto.VendaResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VendaService {

    private final VendaRepository vendaRepository;
    private final VendaMapper vendaMapper;
    private final MedicamentoService medicamentoService;
    private final ClienteService clienteService;
    private final EstoqueService estoqueService;

    @Transactional
    public VendaResponseDTO registrarVenda(VendaRequestDTO request) {
        if (request.getItens() == null || request.getItens().isEmpty()) {
            throw new BusinessException("Não é possível realizar uma venda sem itens.");
        }

        Cliente cliente = clienteService.buscarEntidadePorId(request.getClienteId());

        Venda venda = new Venda();
        venda.setCliente(cliente);
        venda.setDataVenda(LocalDateTime.now());
        venda.setItens(new ArrayList<>());

        BigDecimal totalVenda = BigDecimal.ZERO;

        for (ItemVendaRequestDTO itemDTO : request.getItens()) {
            Medicamento medicamento = medicamentoService.buscarEntidadePorId(itemDTO.getMedicamentoId());

            // 1. Baixa o estoque do Medicamento
            medicamentoService.baixarEstoque(medicamento.getId(), itemDTO.getQuantidade());

            // 2. REGISTRA NO ESTOQUE (Faltava este passo no seu fluxo anterior)
            MovimentacaoRequestDTO mov = new MovimentacaoRequestDTO();
            mov.setMedicamentoId(medicamento.getId());
            mov.setQuantidade(itemDTO.getQuantidade());
            mov.setObservacao("Venda ao cliente: " + cliente.getNome());
            estoqueService.salvarMovimentacaoInterna(medicamento, TipoMovimentacao.SAIDA, mov);

            // 3. Monta o Item da Venda
            ItemVenda item = new ItemVenda();
            item.setVenda(venda);
            item.setMedicamento(medicamento);
            item.setQuantidade(itemDTO.getQuantidade());
            item.setPrecoUnitario(medicamento.getPreco());

            BigDecimal subtotal = item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade()));
            totalVenda = totalVenda.add(subtotal);
            venda.getItens().add(item);
        }

        venda.setValorTotal(totalVenda);
        return vendaMapper.toDTO(vendaRepository.save(venda));
    }

    // MÉTODO QUE ESTAVA FALTANDO
    @Transactional(readOnly = true)
    public List<VendaResponseDTO> listarTodas() {
        return vendaRepository.findAll().stream()
                .map(vendaMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VendaResponseDTO> listarPorCliente(Long clienteId) {
        clienteService.buscarEntidadePorId(clienteId);
        return vendaRepository.findByClienteIdOrderByDataVendaDesc(clienteId).stream()
                .map(vendaMapper::toDTO)
                .toList();
    }
}