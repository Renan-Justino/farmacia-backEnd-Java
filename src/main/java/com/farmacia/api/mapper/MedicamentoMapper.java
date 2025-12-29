package com.farmacia.api.mapper;

import com.farmacia.api.web.medicamento.dto.MedicamentoRequestDTO;
import com.farmacia.api.web.medicamento.dto.MedicamentoResponseDTO;
import com.farmacia.api.model.Medicamento;
import org.springframework.stereotype.Component;

@Component
public class MedicamentoMapper {

    public Medicamento toEntity(MedicamentoRequestDTO dto) {
        if (dto == null) return null;

        Medicamento m = new Medicamento();
        m.setNome(dto.getNome());
        m.setDescricao(dto.getDescricao());
        m.setPreco(dto.getPreco());
        m.setQuantidadeEstoque(dto.getQuantidadeEstoque());
        m.setDataValidade(dto.getDataValidade());

        return m;
    }

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