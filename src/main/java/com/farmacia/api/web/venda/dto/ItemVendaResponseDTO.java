package com.farmacia.api.web.venda.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemVendaResponseDTO {

    private Long medicamentoId;
    private String nomeMedicamento;
    private Integer quantidade;
    private BigDecimal precoUnitario;
}
