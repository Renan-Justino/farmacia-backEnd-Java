package com.farmacia.api.service;

import com.farmacia.api.exception.ResourceNotFoundException;
import com.farmacia.api.exception.business.BusinessException;
import com.farmacia.api.mapper.VendaMapper;
import com.farmacia.api.model.Cliente;
import com.farmacia.api.model.ItemVenda;
import com.farmacia.api.model.Medicamento;
import com.farmacia.api.model.Venda;
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
import java.time.ZoneId;
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
        if (request.getItens() == null || request.getItens().isEmpty())
            throw new BusinessException("Venda deve conter ao menos um item.");

        Cliente cliente = clienteService.buscarEntidadePorId(request.getClienteId());
        Venda venda = criarVenda(cliente);

        BigDecimal totalVenda = BigDecimal.ZERO;

        for (ItemVendaRequestDTO itemDTO : request.getItens()) {
            Medicamento medicamento = medicamentoService.buscarEntidadePorId(itemDTO.getMedicamentoId());

            MovimentacaoRequestDTO mov = new MovimentacaoRequestDTO();
            mov.setMedicamentoId(medicamento.getId());
            mov.setQuantidade(itemDTO.getQuantidade());
            mov.setObservacao("Venda - Cliente: " + cliente.getNome());

            estoqueService.registrarSaida(mov);

            ItemVenda item = criarItemVenda(venda, medicamento, itemDTO.getQuantidade());
            totalVenda = totalVenda.add(item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())));
            venda.getItens().add(item);
        }

        venda.setValorTotal(totalVenda);
        return vendaMapper.toDTO(vendaRepository.save(venda));
    }

    @Transactional(readOnly = true)
    public List<VendaResponseDTO> listarTodas() {
        return vendaRepository.findAll().stream().map(vendaMapper::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<VendaResponseDTO> listarPorCliente(Long clienteId) {
        clienteService.buscarEntidadePorId(clienteId);
        return vendaRepository.findByClienteIdOrderByDataVendaDesc(clienteId)
                .stream()
                .map(vendaMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public VendaResponseDTO buscarPorId(Long id) {
        return vendaRepository.findById(id)
                .map(vendaMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada com o ID: " + id));
    }

    private Venda criarVenda(Cliente cliente) {
        Venda venda = new Venda();
        venda.setCliente(cliente);
        venda.setDataVenda(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
        venda.setItens(new ArrayList<>());
        return venda;
    }

    private ItemVenda criarItemVenda(Venda venda, Medicamento medicamento, int quantidade) {
        ItemVenda item = new ItemVenda();
        item.setVenda(venda);
        item.setMedicamento(medicamento);
        item.setQuantidade(quantidade);
        item.setPrecoUnitario(medicamento.getPreco());
        return item;
    }
}