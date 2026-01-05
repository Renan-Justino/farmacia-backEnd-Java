package com.farmacia.api.web.venda.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "ID do cliente que realiza a compra", example = "0")
    private Long clienteId;

    @NotEmpty(message = "A venda deve possuir pelo menos um item")
    @Schema(description = "Lista de medicamentos e suas quantidades")
    private List<ItemVendaRequestDTO> itens;
}