package com.farmacia.api.mapper;

import com.farmacia.api.model.ItemVenda;
import com.farmacia.api.model.Venda;
import com.farmacia.api.web.venda.dto.ItemVendaResponseDTO;
import com.farmacia.api.web.venda.dto.VendaResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class VendaMapper {

    /**
     * Converte o Aggregate Root Venda para DTO de resposta.
     * Assume que a coleção de itens já está inicializada no contexto transacional.
     */
    public VendaResponseDTO toDTO(Venda venda) {

        List<ItemVendaResponseDTO> itensDTO = venda.getItens()
                .stream()
                .map(this::toItemDTO)
                .collect(Collectors.toList());

        return new VendaResponseDTO(
                venda.getId(),
                venda.getDataVenda(),
                venda.getValorTotal(),
                venda.getCliente().getId(),
                venda.getCliente().getNome(),
                itensDTO
        );
    }

    /**
     * Conversão de ItemVenda para DTO.
     * Utiliza dados imutáveis do momento da venda (preço unitário).
     */
    private ItemVendaResponseDTO toItemDTO(ItemVenda item) {
        return new ItemVendaResponseDTO(
                item.getMedicamento().getId(),
                item.getMedicamento().getNome(),
                item.getQuantidade(),
                item.getPrecoUnitario()
        );
    }
}
