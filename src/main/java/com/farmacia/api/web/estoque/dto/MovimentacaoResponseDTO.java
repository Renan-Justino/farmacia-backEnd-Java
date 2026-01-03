package com.farmacia.api.web.estoque.dto;

import java.time.LocalDateTime;

public record MovimentacaoResponseDTO(
        Long id,
        String medicamentoNome,
        String tipo,
        Integer quantidade,
        String observacao,
        LocalDateTime dataHora
) {}