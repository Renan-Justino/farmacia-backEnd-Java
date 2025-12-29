package com.farmacia.api.service;

import com.farmacia.api.web.venda.dto.ItemVendaRequestDTO;
import com.farmacia.api.web.venda.dto.VendaRequestDTO;
import com.farmacia.api.web.venda.dto.VendaResponseDTO;
import com.farmacia.api.exception.ResourceNotFoundException;
import com.farmacia.api.mapper.VendaMapper;
import com.farmacia.api.model.ItemVenda;
import com.farmacia.api.model.Medicamento;
import com.farmacia.api.model.Venda;
import com.farmacia.api.repository.VendaRepository;
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

    @Transactional
    public VendaResponseDTO registrarVenda(VendaRequestDTO request) {
        Venda venda = new Venda();
        venda.setDataVenda(LocalDateTime.now());
        venda.setItens(new ArrayList<>());

        BigDecimal totalVenda = BigDecimal.ZERO;

        for (ItemVendaRequestDTO itemDTO : request.getItens()) {
            Medicamento medicamento = medicamentoService.buscarEntidadePorId(itemDTO.getMedicamentoId());

            medicamentoService.baixarEstoque(medicamento.getId(), itemDTO.getQuantidade());

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

    @Transactional(readOnly = true)
    public List<VendaResponseDTO> listarTodas() {
        return vendaRepository.findAll().stream()
                .map(vendaMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public VendaResponseDTO buscarPorId(Long id) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada com ID: " + id));
        return vendaMapper.toDTO(venda);
    }
}