package com.farmacia.api.dto.itemVenda;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemVendaRequestDTO {

    private Long medicamentoId;
    private Integer quantidade;
}
