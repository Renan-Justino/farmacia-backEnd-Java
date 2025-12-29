package com.farmacia.api.service;

import com.farmacia.api.exception.ResourceNotFoundException;
import com.farmacia.api.exception.business.*;
import com.farmacia.api.mapper.MedicamentoMapper;
import com.farmacia.api.model.Categoria;
import com.farmacia.api.model.Medicamento;
import com.farmacia.api.repository.CategoriaRepository;
import com.farmacia.api.repository.MedicamentoRepository;
import com.farmacia.api.repository.VendaRepository;
import com.farmacia.api.web.medicamento.dto.MedicamentoRequestDTO;
import com.farmacia.api.web.medicamento.dto.MedicamentoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicamentoService {

    private final MedicamentoRepository medicamentoRepository;
    private final CategoriaRepository categoriaRepository;
    private final VendaRepository vendaRepository;
    private final MedicamentoMapper mapper;

    // --- MÉTODOS DE BUSCA ---

    public Medicamento buscarEntidadePorId(Long id) {
        return medicamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicamento não encontrado com ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<MedicamentoResponseDTO> listarTodos() {
        return medicamentoRepository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public MedicamentoResponseDTO buscarPorId(Long id) {
        return mapper.toDTO(buscarEntidadePorId(id));
    }

    // --- REGRAS DE NEGÓCIO E BLOQUEIOS ---

    @Transactional
    public void baixarEstoque(Long id, Integer quantidade) {
        Medicamento medicamento = buscarEntidadePorId(id);

        if (Boolean.FALSE.equals(medicamento.getAtivo())) {
            throw new MedicamentoInativoException();
        }

        if (medicamento.getDataValidade() != null && medicamento.getDataValidade().isBefore(LocalDate.now())) {
            throw new MedicamentoVencidoException();
        }

        if (medicamento.getQuantidadeEstoque() < quantidade) {
            throw new EstoqueInsuficienteException(medicamento.getNome());
        }

        medicamento.setQuantidadeEstoque(medicamento.getQuantidadeEstoque() - quantidade);
    }

    // --- CRUD OPERAÇÕES ---

    @Transactional
    public MedicamentoResponseDTO cadastrar(MedicamentoRequestDTO request) {
        if (medicamentoRepository.existsByNomeIgnoreCase(request.getNome())) {
            throw new BusinessException("Já existe um medicamento com o nome: " + request.getNome());
        }

        Categoria categoria = buscarCategoria(request.getCategoriaId());
        Medicamento medicamento = mapper.toEntity(request);
        medicamento.setCategoria(categoria);
        medicamento.setAtivo(request.getAtivo() != null ? request.getAtivo() : true);

        return mapper.toDTO(medicamentoRepository.save(medicamento));
    }

    @Transactional
    public MedicamentoResponseDTO atualizar(Long id, MedicamentoRequestDTO request) {
        Medicamento existente = buscarEntidadePorId(id);
        Categoria categoria = buscarCategoria(request.getCategoriaId());

        existente.setNome(request.getNome());
        existente.setDescricao(request.getDescricao());
        existente.setPreco(request.getPreco());
        existente.setQuantidadeEstoque(request.getQuantidadeEstoque());
        existente.setDataValidade(request.getDataValidade());
        existente.setAtivo(request.getAtivo());
        existente.setCategoria(categoria);

        return mapper.toDTO(medicamentoRepository.save(existente));
    }

    @Transactional
    public void alterarStatus(Long id, Boolean novoStatus) {
        Medicamento medicamento = buscarEntidadePorId(id);
        medicamento.setAtivo(novoStatus);
    }

    @Transactional
    public void excluir(Long id) {
        Medicamento medicamento = buscarEntidadePorId(id);

        // Soft Delete: Se houver vendas, apenas inativa. Se não, deleta.
        boolean jaVendido = vendaRepository.existsByItensMedicamentoId(id);

        if (jaVendido) {
            medicamento.setAtivo(false);
        } else {
            medicamentoRepository.delete(medicamento);
        }
    }

    // --- AUXILIARES ---

    private Categoria buscarCategoria(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com ID: " + id));
    }
}