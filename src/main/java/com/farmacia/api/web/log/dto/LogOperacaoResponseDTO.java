package com.farmacia.api.web.log.dto;

import com.farmacia.api.model.LogOperacao;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record LogOperacaoResponseDTO(
    Long id,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime timestamp,
    String level,
    String message,
    String module,
    String action,
    String username,
    Long userId,
    String details,
    String entityType,
    Long entityId
) {
    public static LogOperacaoResponseDTO fromEntity(LogOperacao log) {
        return new LogOperacaoResponseDTO(
            log.getId(),
            log.getTimestamp(),
            log.getLevel().name(),
            log.getMessage(),
            log.getModule(),
            log.getAction(),
            log.getUsername(),
            log.getUserId(),
            log.getDetails(),
            log.getEntityType(),
            log.getEntityId()
        );
    }
}

