package com.farmacia.api.service;

import com.farmacia.api.exception.ResourceNotFoundException;
import com.farmacia.api.exception.business.BusinessException;
import com.farmacia.api.model.Categoria;
import com.farmacia.api.repository.CategoriaRepository;
import com.farmacia.api.repository.MedicamentoRepository;
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
        if (categoriaRepository.existsByNomeIgnoreCase(request.getNome())) {
            throw new BusinessException("Já existe uma categoria com o nome: " + request.getNome());
        }

        Categoria categoria = new Categoria();
        categoria.setNome(request.getNome());
        categoria.setDescricao(request.getDescricao());

        return toDTO(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaResponseDTO atualizar(Long id, CategoriaRequestDTO dto) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com ID: " + id));

        if (categoriaRepository.existsByNomeIgnoreCaseAndIdNot(dto.getNome(), id)) {
            throw new BusinessException("Já existe outra categoria com o nome: " + dto.getNome());
        }

        categoria.setNome(dto.getNome());
        categoria.setDescricao(dto.getDescricao());

        return toDTO(categoriaRepository.save(categoria));
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarTodas() {
        return categoriaRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponseDTO buscarPorId(Long id) {
        return toDTO(categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com ID: " + id)));
    }

    @Transactional
    public void excluir(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoria não encontrada");
        }

        if (medicamentoRepository.existsByCategoriaId(id)) {
            throw new BusinessException("Não é possível excluir uma categoria que possui medicamentos vinculados.");
        }

        categoriaRepository.deleteById(id);
    }

    // Conversão centralizada para manter padrão DTO
    private CategoriaResponseDTO toDTO(Categoria categoria) {
        return new CategoriaResponseDTO(
                categoria.getId(),
                categoria.getNome(),
                categoria.getDescricao()
        );
    }
}
