package com.farmacia.api.service;

import com.farmacia.api.exception.ResourceNotFoundException;
import com.farmacia.api.exception.business.BusinessException;
import com.farmacia.api.exception.business.ClienteMenorDeIdadeException;
import com.farmacia.api.mapper.ClienteMapper;
import com.farmacia.api.model.Cliente;
import com.farmacia.api.repository.ClienteRepository;
import com.farmacia.api.web.cliente.dto.ClienteRequestDTO;
import com.farmacia.api.web.cliente.dto.ClienteResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

/**
 * Camada de serviço para gestão de clientes.
 * Centraliza as regras de negócio e garante a integridade dos dados antes da persistência.
 */
@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository repository;
    private final ClienteMapper mapper;

    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listarTodos() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    public Cliente buscarEntidadePorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + id));
    }

    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarPorId(Long id) {
        return mapper.toDTO(buscarEntidadePorId(id));
    }

    /**
     * Persiste um novo cliente após validar restrições de unicidade e maioridade.
     */
    @Transactional
    public ClienteResponseDTO salvar(ClienteRequestDTO dto) {
        validarUnicidadeCpf(dto.getCpf());
        validarUnicidadeEmail(dto.getEmail());
        validarMaioridade(dto.getDataNascimento());

        Cliente cliente = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(cliente));
    }

    /**
     * Atualiza dados de um cliente existente, validando se novos dados conflitam com registros atuais.
     */
    @Transactional
    public ClienteResponseDTO atualizar(Long id, ClienteRequestDTO dto) {
        Cliente clienteExistente = buscarEntidadePorId(id);

        // Valida se o novo CPF já pertence a outro usuário
        if (!clienteExistente.getCpf().equals(dto.getCpf())) {
            validarUnicidadeCpf(dto.getCpf());
        }

        // Valida se o novo Email já pertence a outro usuário
        if (!clienteExistente.getEmail().equalsIgnoreCase(dto.getEmail())) {
            validarUnicidadeEmail(dto.getEmail());
        }

        validarMaioridade(dto.getDataNascimento());
        mapper.updateEntity(dto, clienteExistente);

        return mapper.toDTO(repository.save(clienteExistente));
    }

    private void validarUnicidadeCpf(String cpf) {
        if (repository.existsByCpf(cpf)) {
            throw new BusinessException("Já existe um cliente cadastrado com este CPF.");
        }
    }

    /**
     * Garante a unicidade do e-mail na base de dados, prevenindo Constraint Violations genéricas (Erro 500).
     */
    private void validarUnicidadeEmail(String email) {
        if (repository.existsByEmail(email)) {
            throw new BusinessException("Já existe um cliente cadastrado com este e-mail.");
        }
    }

    private void validarMaioridade(LocalDate dataNascimento) {
        if (Period.between(dataNascimento, LocalDate.now()).getYears() < 18) {
            throw new ClienteMenorDeIdadeException(
                    "É necessário ter 18 anos ou mais para se cadastrar"
            );
        }
    }
}