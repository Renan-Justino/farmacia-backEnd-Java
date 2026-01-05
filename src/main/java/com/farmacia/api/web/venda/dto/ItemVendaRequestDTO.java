package com.farmacia.api.web.venda.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemVendaRequestDTO {

    @Schema(description = "ID do medicamento no sistema", example = "10")
    private Long medicamentoId;

    @Schema(description = "Quantidade a ser vendida", example = "2")
    private Integer quantidade;
}