package com.farmacia.api.service;

import com.farmacia.api.web.venda.dto.ItemVendaRequestDTO;
import com.farmacia.api.web.venda.dto.VendaRequestDTO;
import com.farmacia.api.web.venda.dto.VendaResponseDTO;
import com.farmacia.api.exception.ResourceNotFoundException;
import com.farmacia.api.mapper.VendaMapper;
import com.farmacia.api.model.ItemVenda;
import com.farmacia.api.model.Medicamento;
import com.farmacia.api.model.Venda;
import com.farmacia.api.repository.MedicamentoRepository;
import com.farmacia.api.repository.VendaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final VendaMapper vendaMapper;
    private final MedicamentoService medicamentoService; // Injetado para baixa de estoque

    public VendaService(VendaRepository vendaRepository,
                        MedicamentoRepository medicamentoRepository,
                        VendaMapper vendaMapper,
                        MedicamentoService medicamentoService) {
        this.vendaRepository = vendaRepository;
        this.medicamentoRepository = medicamentoRepository;
        this.vendaMapper = vendaMapper;
        this.medicamentoService = medicamentoService;
    }

    @Transactional
    public VendaResponseDTO registrarVenda(VendaRequestDTO request) {
        Venda venda = new Venda();
        venda.setDataVenda(LocalDateTime.now());
        venda.setItens(new ArrayList<>());

        BigDecimal totalVenda = BigDecimal.ZERO;

        for (ItemVendaRequestDTO itemDTO : request.getItens()) {
            Medicamento medicamento = medicamentoRepository.findById(itemDTO.getMedicamentoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Medicamento não encontrado com ID: " + itemDTO.getMedicamentoId()));

            // DELEGAÇÃO: Agora o MedicamentoService cuida da validação e baixa
            medicamentoService.baixarEstoque(medicamento.getId(), itemDTO.getQuantidade());

            ItemVenda item = new ItemVenda();
            item.setMedicamento(medicamento);
            item.setQuantidade(itemDTO.getQuantidade());
            item.setPrecoUnitario(medicamento.getPreco());
            item.setVenda(venda);

            BigDecimal subtotal = item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade()));
            totalVenda = totalVenda.add(subtotal);

            venda.getItens().add(item);
        }

        venda.setValorTotal(totalVenda);
        Venda salva = vendaRepository.save(venda);

        return vendaMapper.toDTO(salva);
    }

    @Transactional
    public List<VendaResponseDTO> listarTodas() {
        return vendaRepository.findAll().stream()
                .map(vendaMapper::toDTO)
                .toList();
    }

    @Transactional
    public VendaResponseDTO buscarPorId(Long id) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada com ID: " + id));
        return vendaMapper.toDTO(venda);
    }
}