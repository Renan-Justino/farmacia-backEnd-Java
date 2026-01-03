package com.farmacia.api.mapper;

import com.farmacia.api.web.medicamento.dto.MedicamentoRequestDTO;
import com.farmacia.api.web.medicamento.dto.MedicamentoResponseDTO;
import com.farmacia.api.web.medicamento.dto.MedicamentoUpdateDTO;
import com.farmacia.api.model.Medicamento;
import org.springframework.stereotype.Component;

@Component
public class MedicamentoMapper {

    // USADO NO POST (Cadastro inicial)
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

    // NOVO MÉTODO: USADO NO PUT
    // Este método não recebe e não altera o estoque
    public void updateEntityFromDTO(MedicamentoUpdateDTO dto, Medicamento entity) {
        if (dto == null) return;

        entity.setNome(dto.nome());
        entity.setDescricao(dto.descricao());
        entity.setPreco(dto.preco());
        entity.setDataValidade(dto.dataValidade());
        entity.setAtivo(dto.ativo());
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