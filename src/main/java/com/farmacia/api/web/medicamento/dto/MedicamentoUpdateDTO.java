package com.farmacia.api.web.medicamento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public record MedicamentoUpdateDTO(
        @NotBlank String nome,
        @NotBlank String descricao,
        @NotNull @Positive BigDecimal preco,
        @NotNull LocalDate dataValidade,
        @NotNull Long categoriaId,
        @NotNull Boolean ativo
) {}