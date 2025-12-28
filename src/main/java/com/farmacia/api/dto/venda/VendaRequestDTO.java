package com.farmacia.api.dto.venda;

import com.farmacia.api.dto.itemVenda.ItemVendaRequestDTO;
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

