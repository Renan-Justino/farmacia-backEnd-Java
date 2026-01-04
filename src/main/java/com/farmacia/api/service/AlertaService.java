package com.farmacia.api.service;

import com.farmacia.api.mapper.MedicamentoMapper;
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

    private static final Integer LIMITE_ESTOQUE_BAIXO = 10;
    private static final Integer DIAS_VALIDADE_PROXIMA = 30;

    @Transactional(readOnly = true)
    public List<MedicamentoResponseDTO> listarEstoqueBaixo() {
        return medicamentoRepository
                .findByAtivoTrueAndQuantidadeEstoqueLessThanEqual(LIMITE_ESTOQUE_BAIXO)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MedicamentoResponseDTO> listarValidadeProxima() {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(DIAS_VALIDADE_PROXIMA);

        return medicamentoRepository
                .findByAtivoTrueAndDataValidadeBetween(hoje, limite)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }
}
