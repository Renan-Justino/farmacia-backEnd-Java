package com.farmacia.api.web.venda.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VendaResponseDTO {

    private Long id;
    private LocalDateTime dataVenda;
    private BigDecimal valorTotal;
    private Long clienteId;
    private String nomeCliente;
    private List<ItemVendaResponseDTO> itens;
}