package com.farmacia.api.web.estoque.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MovimentacaoRequestDTO {
    @NotNull(message = "ID do medicamento é obrigatório")
    @Schema(description = "MedicamentoId", example = "0")
    private Long medicamentoId;

    @NotNull(message = "Quantidade é obrigatória")
    @Positive(message = "A quantidade deve ser maior que zero")
    @Schema(description = "Quantidade", example = "0")
    private Integer quantidade;

    private String observacao;
}