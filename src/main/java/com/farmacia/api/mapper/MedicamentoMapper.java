package com.farmacia.api.mapper;

import com.farmacia.api.model.Medicamento;
import com.farmacia.api.web.medicamento.dto.MedicamentoRequestDTO;
import com.farmacia.api.web.medicamento.dto.MedicamentoResponseDTO;
import com.farmacia.api.web.medicamento.dto.MedicamentoUpdateDTO;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class MedicamentoMapper {

    /**
     * Converte DTO de criação para entidade.
     * Define 'ativo' como true se não informado.
     * Usado no POST /medicamentos
     */
    public Medicamento toEntity(MedicamentoRequestDTO dto) {
        if (dto == null) return null;

        Medicamento m = new Medicamento();
        m.setNome(dto.getNome());
        m.setDescricao(dto.getDescricao());
        m.setPreco(dto.getPreco());
        m.setQuantidadeEstoque(dto.getQuantidadeEstoque());
        m.setDataValidade(dto.getDataValidade());
        m.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);

        return m;
    }

    /**
     * Atualiza entidade existente a partir do DTO de update.
     * Não altera estoque nem outros campos controlados pelo domínio.
     * Usado no PUT /medicamentos/{id}
     */
    public void updateEntityFromDTO(MedicamentoUpdateDTO dto, Medicamento entity) {
        Objects.requireNonNull(dto, "MedicamentoUpdateDTO não pode ser nulo");
        Objects.requireNonNull(entity, "Medicamento não pode ser nulo");

        entity.setNome(dto.nome());
        entity.setDescricao(dto.descricao());
        entity.setPreco(dto.preco());
        entity.setDataValidade(dto.dataValidade());
        entity.setAtivo(dto.ativo());
    }

    /**
     * Converte entidade para DTO de resposta.
     * Inclui dados da categoria, se existir.
     */
    public MedicamentoResponseDTO toDTO(Medicamento entity) {
        if (entity == null) return null;

        Long catId = (entity.getCategoria() != null) ? entity.getCategoria().getId() : null;
        String catNome = (entity.getCategoria() != null) ? entity.getCategoria().getNome() : null;

        return new MedicamentoResponseDTO(
                entity.getId(),
                entity.getNome(),
                entity.getDescricao(),
                entity.getPreco(),
                entity.getQuantidadeEstoque(),
                entity.getDataValidade(),
                entity.getAtivo(),
                catId,
                catNome
        );
    }
}
