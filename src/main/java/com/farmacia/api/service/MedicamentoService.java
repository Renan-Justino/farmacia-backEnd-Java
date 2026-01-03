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
import com.farmacia.api.web.medicamento.dto.MedicamentoUpdateDTO;
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

    public Medicamento buscarEntidadePorId(Long id) {
        return medicamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicamento não encontrado com ID: " + id));
    }

    @Transactional
    public MedicamentoResponseDTO cadastrar(MedicamentoRequestDTO request) {
        // Validações preventivas de negócio (Fail-fast)
        if (Boolean.FALSE.equals(request.getAtivo())) {
            throw new BusinessException("Não é permitido cadastrar um medicamento já inativo.");
        }

        if (medicamentoRepository.existsByNomeIgnoreCase(request.getNome())) {
            throw new BusinessException("Já existe um medicamento com o nome: " + request.getNome());
        }

        validarDataValidade(request.getDataValidade());

        Categoria categoria = buscarCategoria(request.getCategoriaId());
        Medicamento medicamento = mapper.toEntity(request);
        medicamento.setCategoria(categoria);

        // Garante estado consistente do objeto
        if (medicamento.getAtivo() == null) medicamento.setAtivo(true);

        return mapper.toDTO(medicamentoRepository.save(medicamento));
    }

    @Transactional
    public MedicamentoResponseDTO atualizar(Long id, MedicamentoUpdateDTO request) {
        // Orquestração: Busca dependências externas
        Medicamento existente = buscarEntidadePorId(id);
        Categoria categoria = buscarCategoria(request.categoriaId());

        validarDataValidade(request.dataValidade());

        // Modelo Rico: A entidade encapsula sua própria lógica de mutação de estado
        // O estoque é preservado pois o método atualizarDados não o acessa
        existente.atualizarDados(request, categoria);

        return mapper.toDTO(medicamentoRepository.save(existente));
    }

    @Transactional
    public void baixarEstoque(Long id, Integer quantidade) {
        Medicamento medicamento = buscarEntidadePorId(id);

        if (Boolean.FALSE.equals(medicamento.getAtivo())) throw new MedicamentoInativoException();
        validarDataValidade(medicamento.getDataValidade());

        if (medicamento.getQuantidadeEstoque() < quantidade) {
            throw new EstoqueInsuficienteException(medicamento.getNome());
        }

        medicamento.setQuantidadeEstoque(medicamento.getQuantidadeEstoque() - quantidade);
    }

    @Transactional
    public void alterarStatus(Long id, Boolean novoStatus) {
        Medicamento medicamento = buscarEntidadePorId(id);
        medicamento.setAtivo(novoStatus);
    }

    @Transactional
    public void excluir(Long id) {
        Medicamento medicamento = buscarEntidadePorId(id);

        // Soft-delete se houver histórico de vendas; caso contrário, remoção física
        if (vendaRepository.existsByItensMedicamentoId(id)) {
            medicamento.setAtivo(false);
        } else {
            medicamentoRepository.delete(medicamento);
        }
    }

    private void validarDataValidade(LocalDate data) {
        if (data != null && data.isBefore(LocalDate.now())) throw new MedicamentoVencidoException();
    }

    private Categoria buscarCategoria(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com ID: " + id));
    }
}