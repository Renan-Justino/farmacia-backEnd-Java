package com.farmacia.api.mapper;

import com.farmacia.api.model.ItemVenda;
import com.farmacia.api.model.Venda;
import com.farmacia.api.web.venda.dto.ItemVendaResponseDTO;
import com.farmacia.api.web.venda.dto.VendaResponseDTO;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Mapper para o Aggregate Root Venda.
 * Responsável por converter entidades para DTOs de resposta.
 * Mantém imutabilidade e não expõe dados sensíveis do domínio.
 */
@Component
public class VendaMapper {

    /**
     * Converte a entidade Venda para DTO de resposta.
     * Assume que a coleção de itens está inicializada no contexto transacional.
     *
     * @param venda entidade de venda
     * @return DTO de venda pronto para resposta
     */
    public VendaResponseDTO toDTO(Venda venda) {
        Objects.requireNonNull(venda, "Venda não pode ser nula");

        List<ItemVendaResponseDTO> itensDTO = venda.getItens() != null
                ? venda.getItens().stream()
                .map(this::toItemDTO)
                .collect(Collectors.toList())
                : Collections.emptyList();

        return new VendaResponseDTO(
                venda.getId(),
                venda.getDataVenda(),
                venda.getValorTotal(),
                venda.getCliente() != null ? venda.getCliente().getId() : null,
                venda.getCliente() != null ? venda.getCliente().getNome() : null,
                itensDTO
        );
    }

    /**
     * Converte ItemVenda para DTO de item.
     * Usa dados do momento da venda (preço unitário) e evita vazamento de entidade relacionada.
     *
     * @param item ItemVenda
     * @return DTO do item
     */
    private ItemVendaResponseDTO toItemDTO(ItemVenda item) {
        Objects.requireNonNull(item, "ItemVenda não pode ser nulo");
        return new ItemVendaResponseDTO(
                item.getMedicamento() != null ? item.getMedicamento().getId() : null,
                item.getMedicamento() != null ? item.getMedicamento().getNome() : null,
                item.getQuantidade(),
                item.getPrecoUnitario()
        );
    }
}
