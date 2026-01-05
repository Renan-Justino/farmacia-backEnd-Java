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

    @Transactional(readOnly = true)
    public List<MedicamentoResponseDTO> listarEstoqueBaixo(int limite) {
        return medicamentoRepository
                .findByAtivoTrueAndQuantidadeEstoqueLessThanEqual(limite)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MedicamentoResponseDTO> listarValidadeProxima(int dias) {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(dias);

        return medicamentoRepository
                .findByAtivoTrueAndDataValidadeBetween(hoje, limite)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }
}
