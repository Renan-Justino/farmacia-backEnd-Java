package com.farmacia.api.mapper;

import com.farmacia.api.web.venda.dto.ItemVendaResponseDTO;
import com.farmacia.api.web.venda.dto.VendaResponseDTO;
import com.farmacia.api.model.ItemVenda;
import com.farmacia.api.model.Venda;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class VendaMapper {

    // Converte de Entity para Response (Saída para o Controller)
    public VendaResponseDTO toDTO(Venda entity) {
        if (entity == null) return null;

        List<ItemVendaResponseDTO> itensDTO = entity.getItens().stream()
                .map(this::toItemDTO)
                .collect(Collectors.toList());

        // O construtor do DTO não deve mais esperar o StatusVenda
        return new VendaResponseDTO(
                entity.getId(),
                entity.getDataVenda(),
                entity.getValorTotal(),
                itensDTO
        );
    }

    private ItemVendaResponseDTO toItemDTO(ItemVenda entity) {
        return new ItemVendaResponseDTO(
                entity.getMedicamento().getId(),
                entity.getMedicamento().getNome(),
                entity.getQuantidade(),
                entity.getPrecoUnitario()
        );
    }
}