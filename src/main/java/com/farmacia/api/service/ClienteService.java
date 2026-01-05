package com.farmacia.api.service;

import com.farmacia.api.exception.ResourceNotFoundException;
import com.farmacia.api.exception.business.BusinessException;
import com.farmacia.api.exception.business.ClienteMenorDeIdadeException;
import com.farmacia.api.mapper.ClienteMapper;
import com.farmacia.api.model.Cliente;
import com.farmacia.api.repository.ClienteRepository;
import com.farmacia.api.web.cliente.dto.ClienteRequestDTO;
import com.farmacia.api.web.cliente.dto.ClienteResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Serviço para gerenciamento de clientes.
 * Centraliza regras de negócio, validações e integridade antes da persistência.
 */
@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository repository;
    private final ClienteMapper mapper;

    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarPorId(Long id) {
        return mapper.toDTO(buscarEntidadePorId(id));
    }

    public Cliente buscarEntidadePorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + id));
    }

    /**
     * Salva um novo cliente após validações.
     */
    @Transactional
    public ClienteResponseDTO salvar(@Valid ClienteRequestDTO dto) {
        validarUnicidadeCpf(dto.getCpf(), null);
        validarUnicidadeEmail(dto.getEmail(), null);
        validarMaioridade(dto.getDataNascimento());

        Cliente cliente = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(cliente));
    }

    /**
     * Atualiza um cliente existente.
     */
    @Transactional
    public ClienteResponseDTO atualizar(Long id, @Valid ClienteRequestDTO dto) {
        Cliente clienteExistente = buscarEntidadePorId(id);

        validarUnicidadeCpf(dto.getCpf(), id);
        validarUnicidadeEmail(dto.getEmail(), id);
        validarMaioridade(dto.getDataNascimento());

        mapper.updateEntity(dto, clienteExistente);
        return mapper.toDTO(repository.save(clienteExistente));
    }

    /**
     * Valida se o CPF é único.
     * @param cpf valor a validar
     * @param idExistente id do cliente existente (para update), null se novo registro
     */
    private void validarUnicidadeCpf(String cpf, Long idExistente) {
        boolean existe = (idExistente == null)
                ? repository.existsByCpf(cpf)
                : repository.existsByCpfAndIdNot(cpf, idExistente);

        if (existe) {
            throw new BusinessException("Já existe um cliente cadastrado com este CPF.");
        }
    }

    /**
     * Valida se o e-mail é único.
     */
    private void validarUnicidadeEmail(String email, Long idExistente) {
        boolean existe = (idExistente == null)
                ? repository.existsByEmail(email)
                : repository.existsByEmailAndIdNot(email, idExistente);

        if (existe) {
            throw new BusinessException("Já existe um cliente cadastrado com este e-mail.");
        }
    }

    /**
     * Garante que o cliente tenha 18 anos ou mais.
     */
    private void validarMaioridade(LocalDate dataNascimento) {
        if (dataNascimento.isAfter(LocalDate.now().minusYears(18))) {
            throw new ClienteMenorDeIdadeException(
                    "É necessário ter 18 anos ou mais para se cadastrar"
            );
        }
    }
}
