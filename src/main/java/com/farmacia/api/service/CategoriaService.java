package com.farmacia.api.service;

import com.farmacia.api.exception.ResourceNotFoundException;
import com.farmacia.api.exception.business.BusinessException;
import com.farmacia.api.model.Categoria;
import com.farmacia.api.repository.CategoriaRepository;
import com.farmacia.api.repository.MedicamentoRepository; // Necessário para a trava de exclusão
import com.farmacia.api.web.categoria.dto.CategoriaRequestDTO;
import com.farmacia.api.web.categoria.dto.CategoriaResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final MedicamentoRepository medicamentoRepository;

    @Transactional
    public CategoriaResponseDTO cadastrar(CategoriaRequestDTO request) {
        // Regra: Nome Único (Fail-Fast)
        if (categoriaRepository.existsByNomeIgnoreCase(request.getNome())) {
            throw new BusinessException("Já existe uma categoria com o nome: " + request.getNome());
        }

        Categoria categoria = new Categoria();
        categoria.setNome(request.getNome());
        categoria.setDescricao(request.getDescricao());

        return toResponseDTO(categoriaRepository.save(categoria));
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarTodas() {
        return categoriaRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponseDTO buscarPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com ID: " + id));
        return toResponseDTO(categoria);
    }

    @Transactional
    public void excluir(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoria não encontrada");
        }

        // Regra Sênior: Bloquear exclusão se houver medicamentos vinculados
        // existsByCategoriaId é um query method padrão do Spring Data no MedicamentoRepository
        if (medicamentoRepository.existsByCategoriaId(id)) {
            throw new BusinessException("Não é possível excluir uma categoria que possui medicamentos vinculados.");
        }

        categoriaRepository.deleteById(id);
    }

    private CategoriaResponseDTO toResponseDTO(Categoria categoria) {
        CategoriaResponseDTO dto = new CategoriaResponseDTO();
        dto.setId(categoria.getId());
        dto.setNome(categoria.getNome());
        dto.setDescricao(categoria.getDescricao());
        return dto;
    }
}