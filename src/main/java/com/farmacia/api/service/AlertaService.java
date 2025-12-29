package com.farmacia.api.service;

import com.farmacia.api.mapper.MedicamentoMapper;
import com.farmacia.api.model.Medicamento;
import com.farmacia.api.repository.MedicamentoRepository;
import com.farmacia.api.web.medicamento.dto.MedicamentoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertaService {

    private final MedicamentoRepository medicamentoRepository;
    private final MedicamentoMapper mapper;

    // Configurações de Alerta (Poderiam vir do application.properties)
    private static final Integer LIMITE_ESTOQUE_BAIXO = 10;
    private static final Integer DIAS_VALIDADE_PROXIMA = 30;

    @Transactional(readOnly = true)
    public List<MedicamentoResponseDTO> listarEstoqueBaixo() {
        // Buscamos medicamentos ativos com estoque menor ou igual ao limite
        return medicamentoRepository.findAll().stream()
                .filter(Medicamento::getAtivo)
                .filter(m -> m.getQuantidadeEstoque() <= LIMITE_ESTOQUE_BAIXO)
                .map(mapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MedicamentoResponseDTO> listarValidadeProxima() {
        LocalDate limiteData = LocalDate.now().plusDays(DIAS_VALIDADE_PROXIMA);

        // Buscamos medicamentos ativos, não vencidos, mas que vencem nos próximos 30 dias
        return medicamentoRepository.findAll().stream()
                .filter(Medicamento::getAtivo)
                .filter(m -> m.getDataValidade() != null)
                .filter(m -> m.getDataValidade().isAfter(LocalDate.now().minusDays(1))) // Não vencido ainda
                .filter(m -> m.getDataValidade().isBefore(limiteData))
                .map(mapper::toDTO)
                .toList();
    }
}