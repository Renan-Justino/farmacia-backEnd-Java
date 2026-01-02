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

    // Retorna a ENTIDADE para uso interno entre Services
    public Cliente buscarEntidadePorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + id));
    }

    // Retorna o DTO para o Controller
    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarPorId(Long id) {
        return mapper.toDTO(buscarEntidadePorId(id));
    }

    @Transactional
    public ClienteResponseDTO salvar(ClienteRequestDTO dto) {
        validarUnicidadeCpf(dto.getCpf());
        validarMaioridade(dto.getDataNascimento());

        Cliente cliente = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(cliente));
    }

    @Transactional
    public ClienteResponseDTO atualizar(Long id, ClienteRequestDTO dto) {
        Cliente clienteExistente = buscarEntidadePorId(id);

        if (!clienteExistente.getCpf().equals(dto.getCpf())) {
            validarUnicidadeCpf(dto.getCpf());
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

    private void validarMaioridade(LocalDate dataNascimento) {
        if (Period.between(dataNascimento, LocalDate.now()).getYears() < 18) {
            throw new ClienteMenorDeIdadeException(
                    "É necessário ter 18 anos ou mais para se cadastrar"
            );
        }
    }
}