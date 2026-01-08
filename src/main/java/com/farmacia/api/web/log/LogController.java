package com.farmacia.api.web.log;

import com.farmacia.api.model.LogOperacao;
import com.farmacia.api.service.LogService;
import com.farmacia.api.web.log.dto.LogOperacaoResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
@Tag(name = "Logs")
public class LogController {

    private final LogService logService;

    @GetMapping
    @Operation(summary = "Listar logs de auditoria", description = "Retorna todos os logs do sistema para auditoria, opcionalmente filtrados por nível e período.")
    public ResponseEntity<List<LogOperacaoResponseDTO>> listar(
        @RequestParam(required = false) String level,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        List<LogOperacao> logs;

        // Se não há filtros de data, retorna todos os logs (histórico completo)
        if (startDate == null || endDate == null) {
            if (level != null && !level.isEmpty()) {
                try {
                    LogOperacao.NivelLog nivelLog = LogOperacao.NivelLog.valueOf(level.toUpperCase());
                    logs = logService.listarPorNivel(nivelLog);
                } catch (IllegalArgumentException e) {
                    logs = logService.listarTodos();
                }
            } else {
                logs = logService.listarTodos();
            }
        } else {
            // Com filtro de data
            if (level != null && !level.isEmpty()) {
                try {
                    LogOperacao.NivelLog nivelLog = LogOperacao.NivelLog.valueOf(level.toUpperCase());
                    logs = logService.listarPorNivelEPeriodo(nivelLog, startDate, endDate);
                } catch (IllegalArgumentException e) {
                    logs = logService.listarPorPeriodo(startDate, endDate);
                }
            } else {
                logs = logService.listarPorPeriodo(startDate, endDate);
            }
        }

        // Converter para DTO para garantir serialização correta
        List<LogOperacaoResponseDTO> response = logs.stream()
            .map(LogOperacaoResponseDTO::fromEntity)
            .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}

