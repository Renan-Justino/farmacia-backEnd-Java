package com.farmacia.api.web.venda.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VendaRequestDTO {

    private List<ItemVendaRequestDTO> itens;
}

