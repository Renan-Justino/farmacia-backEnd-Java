package com.farmacia.api.mapper;

import com.farmacia.api.model.Cliente;
import com.farmacia.api.web.cliente.dto.ClienteRequestDTO;
import com.farmacia.api.web.cliente.dto.ClienteResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    /**
     * Converte entidade Cliente para DTO de resposta.
     * Não expõe campos de controle interno (ex: ativo).
     */
    public ClienteResponseDTO toDTO(Cliente entity) {
        if (entity == null) {
            return null;
        }

        return new ClienteResponseDTO(
                entity.getId(),
                entity.getNome(),
                entity.getCpf(),
                entity.getEmail(),
                entity.getDataNascimento()
        );
    }

    /**
     * Cria uma nova entidade Cliente a partir do DTO de criação.
     * Campos gerenciados pelo domínio (id, ativo) não são atribuídos aqui.
     */
    public Cliente toEntity(ClienteRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Cliente cliente = new Cliente();
        cliente.setNome(dto.getNome());
        cliente.setCpf(dto.getCpf());
        cliente.setEmail(dto.getEmail());
        cliente.setDataNascimento(dto.getDataNascimento());

        return cliente;
    }

    /**
     * Atualiza uma entidade existente.
     * Método separado evita sobrescrita indevida de estado persistente.
     */
    public void updateEntity(ClienteRequestDTO dto, Cliente entity) {
        entity.setNome(dto.getNome());
        entity.setCpf(dto.getCpf());
        entity.setEmail(dto.getEmail());
        entity.setDataNascimento(dto.getDataNascimento());
    }
}
