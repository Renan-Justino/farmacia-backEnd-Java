package com.farmacia.api.web.venda.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VendaRequestDTO {

    @NotNull(message = "O cliente é obrigatório")
    private Long clienteId;

    @NotEmpty(message = "A venda deve possuir pelo menos um item")
    private List<ItemVendaRequestDTO> itens;
}