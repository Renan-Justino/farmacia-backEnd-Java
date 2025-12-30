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

    public VendaResponseDTO toDTO(Venda venda) {
        List<ItemVendaResponseDTO> itensDTO = venda.getItens().stream()
                .map(this::toItemDTO)
                .collect(Collectors.toList());

        // CORREÇÃO: Passando os 6 parâmetros que o VendaResponseDTO exige
        return new VendaResponseDTO(
                venda.getId(),
                venda.getDataVenda(),
                venda.getValorTotal(),
                venda.getCliente().getId(),      // 4º parâmetro: clienteId
                venda.getCliente().getNome(),    // 5º parâmetro: nomeCliente
                itensDTO                         // 6º parâmetro: itens
        );
    }

    private ItemVendaResponseDTO toItemDTO(ItemVenda item) {
        return new ItemVendaResponseDTO(
                item.getMedicamento().getId(),
                item.getMedicamento().getNome(),
                item.getQuantidade(),
                item.getPrecoUnitario()
        );
    }
}