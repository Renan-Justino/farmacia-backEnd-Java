package com.farmacia.api.service;

import com.farmacia.api.exception.ResourceNotFoundException;
import com.farmacia.api.exception.business.*;
import com.farmacia.api.mapper.VendaMapper;
import com.farmacia.api.model.*;
import com.farmacia.api.repository.VendaRepository;
import com.farmacia.api.web.estoque.dto.MovimentacaoRequestDTO;
import com.farmacia.api.web.venda.dto.*;
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

    /**
     * Boundary transacional da venda.
     * Garante atomicidade entre validações, baixa de estoque e persistência.
     */
    @Transactional
    public VendaResponseDTO registrarVenda(VendaRequestDTO request) {

        if (request.getItens() == null || request.getItens().isEmpty()) {
            throw new BusinessException("Não é possível realizar uma venda sem itens.");
        }

        Cliente cliente = clienteService.buscarEntidadePorId(request.getClienteId());

        Venda venda = new Venda();
        venda.setCliente(cliente);
        venda.setDataVenda(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
        venda.setItens(new ArrayList<>());

        BigDecimal totalVenda = BigDecimal.ZERO;

        for (ItemVendaRequestDTO itemDTO : request.getItens()) {

            Medicamento medicamento = medicamentoService.buscarEntidadePorId(itemDTO.getMedicamentoId());

            if (itemDTO.getQuantidade() <= 0) {
                throw new BusinessException(
                        "A quantidade do medicamento " + medicamento.getNome() + " deve ser maior que zero."
                );
            }

            if (Boolean.FALSE.equals(medicamento.getAtivo())) {
                throw new MedicamentoInativoException();
            }

            // Validação baseada na data da venda, não na data atual do sistema
            if (medicamento.getDataValidade() != null &&
                    medicamento.getDataValidade().isBefore(venda.getDataVenda().toLocalDate())) {
                throw new MedicamentoVencidoException();
            }

            // Baixa de estoque ocorre antes do commit para garantir consistência
            MovimentacaoRequestDTO mov = new MovimentacaoRequestDTO();
            mov.setMedicamentoId(medicamento.getId());
            mov.setQuantidade(itemDTO.getQuantidade());
            mov.setObservacao("Venda - Cliente: " + cliente.getNome());
            estoqueService.registrarSaida(mov);

            ItemVenda item = new ItemVenda();
            item.setVenda(venda);
            item.setMedicamento(medicamento);
            item.setQuantidade(itemDTO.getQuantidade());
            item.setPrecoUnitario(medicamento.getPreco());

            BigDecimal subtotal =
                    item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade()));

            totalVenda = totalVenda.add(subtotal);
            venda.getItens().add(item);
        }

        venda.setValorTotal(totalVenda);

        // Persistência única do Aggregate Root (cascade cuida dos itens)
        return vendaMapper.toDTO(vendaRepository.save(venda));
    }

    @Transactional(readOnly = true)
    public List<VendaResponseDTO> listarTodas() {
        return vendaRepository.findAll()
                .stream()
                .map(vendaMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VendaResponseDTO> listarPorCliente(Long clienteId) {
        clienteService.buscarEntidadePorId(clienteId);
        return vendaRepository
                .findByClienteIdOrderByDataVendaDesc(clienteId)
                .stream()
                .map(vendaMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public VendaResponseDTO buscarPorId(Long id) {
        return vendaRepository.findById(id)
                .map(vendaMapper::toDTO)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Venda não encontrada com o ID: " + id)
                );
    }
}
