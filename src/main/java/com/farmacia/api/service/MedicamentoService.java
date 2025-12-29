package com.farmacia.api.service;

import com.farmacia.api.web.medicamento.dto.MedicamentoRequestDTO;
import com.farmacia.api.web.medicamento.dto.MedicamentoResponseDTO;
import com.farmacia.api.exception.business.BusinessException; // Importe sua exception
import com.farmacia.api.exception.ResourceNotFoundException; // Importe sua exception
import com.farmacia.api.mapper.MedicamentoMapper;
import com.farmacia.api.model.Categoria;
import com.farmacia.api.model.Medicamento;
import com.farmacia.api.repository.CategoriaRepository;
import com.farmacia.api.repository.MedicamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MedicamentoService {

    private final MedicamentoRepository medicamentoRepository;
    private final CategoriaRepository categoriaRepository;
    private final MedicamentoMapper mapper;

    public MedicamentoService(MedicamentoRepository medicamentoRepository,
                              CategoriaRepository categoriaRepository,
                              MedicamentoMapper mapper) {
        this.medicamentoRepository = medicamentoRepository;
        this.categoriaRepository = categoriaRepository;
        this.mapper = mapper;
    }

    // NOVA LÓGICA DE ESTOQUE REATORADA
    @Transactional
    public void baixarEstoque(Long id, Integer quantidade) {
        Medicamento medicamento = medicamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicamento não encontrado com ID: " + id));

        if (medicamento.getQuantidadeEstoque() < quantidade) {
            throw new BusinessException("Estoque insuficiente para o medicamento: " + medicamento.getNome());
        }

        medicamento.setQuantidadeEstoque(medicamento.getQuantidadeEstoque() - quantidade);
        medicamentoRepository.save(medicamento);
    }

    @Transactional
    public MedicamentoResponseDTO cadastrar(MedicamentoRequestDTO request) {
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        Medicamento medicamento = mapper.toEntity(request);
        medicamento.setCategoria(categoria);
        medicamento.setAtivo(request.getAtivo() != null ? request.getAtivo() : true);

        validarMedicamento(medicamento);
        return mapper.toDTO(medicamentoRepository.save(medicamento));
    }

    public List<MedicamentoResponseDTO> listarTodos() {
        return medicamentoRepository.findAll().stream().map(mapper::toDTO).toList();
    }

    public MedicamentoResponseDTO buscarPorId(Long id) {
        Medicamento medicamento = medicamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicamento não encontrado"));
        return mapper.toDTO(medicamento);
    }

    @Transactional
    public MedicamentoResponseDTO atualizar(Long id, MedicamentoRequestDTO request) {
        Medicamento existente = medicamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicamento não encontrado"));

        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        existente.setNome(request.getNome());
        existente.setDescricao(request.getDescricao());
        existente.setPreco(request.getPreco());
        existente.setQuantidadeEstoque(request.getQuantidadeEstoque());
        existente.setDataValidade(request.getDataValidade());
        existente.setAtivo(request.getAtivo());
        existente.setCategoria(categoria);

        validarMedicamento(existente);
        return mapper.toDTO(medicamentoRepository.save(existente));
    }

    public void excluir(Long id) {
        if (!medicamentoRepository.existsById(id)) {
            throw new RuntimeException("Medicamento não encontrado");
        }
        medicamentoRepository.deleteById(id);
    }

    private void validarMedicamento(Medicamento medicamento) {
        if (medicamento.getNome() == null || medicamento.getNome().isBlank()) {
            throw new RuntimeException("Nome do medicamento é obrigatório");
        }
        if (medicamento.getPreco() == null || medicamento.getPreco().signum() < 0) {
            throw new RuntimeException("Preço inválido");
        }
        if (medicamento.getQuantidadeEstoque() == null || medicamento.getQuantidadeEstoque() < 0) {
            throw new RuntimeException("Quantidade em estoque inválida");
        }
        if (medicamento.getCategoria() == null) {
            throw new RuntimeException("Categoria é obrigatória");
        }
    }
}