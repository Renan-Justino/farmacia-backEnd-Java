package com.farmacia.api.service;

import com.farmacia.api.exception.business.EstoqueInsuficienteException;
import com.farmacia.api.model.Medicamento;
import com.farmacia.api.model.MovimentacaoEstoque;
import com.farmacia.api.model.enums.TipoMovimentacao;
import com.farmacia.api.repository.MovimentacaoEstoqueRepository;
import com.farmacia.api.web.estoque.dto.MovimentacaoRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final MovimentacaoEstoqueRepository repository;
    private final MedicamentoService medicamentoService;

    @Transactional(readOnly = true)
    public List<MovimentacaoEstoque> listarPorMedicamento(Long medicamentoId) {
        medicamentoService.buscarEntidadePorId(medicamentoId);
        return repository.findByMedicamentoIdOrderByDataHoraDesc(medicamentoId);
    }

    @Transactional
    public void registrarEntrada(MovimentacaoRequestDTO dto) {
        Medicamento medicamento = medicamentoService.buscarEntidadePorId(dto.getMedicamentoId());
        medicamento.setQuantidadeEstoque(medicamento.getQuantidadeEstoque() + dto.getQuantidade());
        salvarMovimentacao(medicamento, TipoMovimentacao.ENTRADA, dto);
    }

    @Transactional
    public void registrarSaida(MovimentacaoRequestDTO dto) {
        Medicamento medicamento = medicamentoService.buscarEntidadePorId(dto.getMedicamentoId());
        if (medicamento.getQuantidadeEstoque() < dto.getQuantidade()) {
            throw new EstoqueInsuficienteException(medicamento.getNome());
        }
        medicamento.setQuantidadeEstoque(medicamento.getQuantidadeEstoque() - dto.getQuantidade());
        salvarMovimentacao(medicamento, TipoMovimentacao.SAIDA, dto);
    }

    // Método que o VendaService usa para registrar o histórico
    @Transactional
    public void salvarMovimentacaoInterna(Medicamento m, TipoMovimentacao tipo, MovimentacaoRequestDTO dto) {
        salvarMovimentacao(m, tipo, dto);
    }

    @Transactional
    public void salvarMovimentacao(Medicamento m, TipoMovimentacao tipo, MovimentacaoRequestDTO dto) {
        MovimentacaoEstoque mov = new MovimentacaoEstoque();
        mov.setMedicamento(m);
        mov.setTipo(tipo);
        mov.setQuantidade(dto.getQuantidade());
        mov.setObservacao(dto.getObservacao());
        repository.save(mov);
    }
}