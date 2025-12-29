package com.farmacia.api.web.estoque.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MovimentacaoRequestDTO {
    @NotNull(message = "ID do medicamento é obrigatório")
    private Long medicamentoId;

    @NotNull(message = "Quantidade é obrigatória")
    @Positive(message = "A quantidade deve ser maior que zero")
    private Integer quantidade;

    private String observacao;
}