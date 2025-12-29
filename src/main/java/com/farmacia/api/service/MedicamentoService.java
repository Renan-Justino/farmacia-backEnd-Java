package com.farmacia.api.service;

import com.farmacia.api.exception.ResourceNotFoundException;
import com.farmacia.api.exception.business.EstoqueInsuficienteException;
import com.farmacia.api.exception.business.MedicamentoInativoException;
import com.farmacia.api.exception.business.MedicamentoVencidoException;
import com.farmacia.api.mapper.MedicamentoMapper;
import com.farmacia.api.model.Categoria;
import com.farmacia.api.model.Medicamento;
import com.farmacia.api.repository.CategoriaRepository;
import com.farmacia.api.repository.MedicamentoRepository;
import com.farmacia.api.web.medicamento.dto.MedicamentoRequestDTO;
import com.farmacia.api.web.medicamento.dto.MedicamentoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor // Construtor limpo via Lombok para campos final
public class MedicamentoService {

    private final MedicamentoRepository medicamentoRepository;
    private final CategoriaRepository categoriaRepository;
    private final MedicamentoMapper mapper;

    /**
     * Busca a entidade para uso interno.
     * Centraliza a exceção 404 em um único lugar.
     */
    public Medicamento buscarEntidadePorId(Long id) {
        return medicamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicamento não encontrado com ID: " + id));
    }

    @Transactional
    public void baixarEstoque(Long id, Integer quantidade) {
        Medicamento medicamento = buscarEntidadePorId(id);

        // Fail-Fast: Validações antes de qualquer operação
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
        // Em métodos @Transactional, o save() é implícito ao fim do método (Dirty Checking)
    }

    @Transactional
    public MedicamentoResponseDTO cadastrar(MedicamentoRequestDTO request) {
        Categoria categoria = buscarCategoria(request.getCategoriaId());

        Medicamento medicamento = mapper.toEntity(request);
        medicamento.setCategoria(categoria);
        medicamento.setAtivo(request.getAtivo() != null ? request.getAtivo() : true);

        return mapper.toDTO(medicamentoRepository.save(medicamento));
    }

    @Transactional(readOnly = true) // Otimiza o flush do Hibernate e melhora a leitura
    public List<MedicamentoResponseDTO> listarTodos() {
        return medicamentoRepository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public MedicamentoResponseDTO buscarPorId(Long id) {
        return mapper.toDTO(buscarEntidadePorId(id));
    }

    @Transactional
    public MedicamentoResponseDTO atualizar(Long id, MedicamentoRequestDTO request) {
        Medicamento existente = buscarEntidadePorId(id);
        Categoria categoria = buscarCategoria(request.getCategoriaId());

        // Sênior: Atualização direta e consistente
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
    public void excluir(Long id) {
        if (!medicamentoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Medicamento não encontrado");
        }
        medicamentoRepository.deleteById(id);
    }

    // Método privado auxiliar para evitar repetição de código (DRY - Don't Repeat Yourself)
    private Categoria buscarCategoria(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com ID: " + id));
    }
}