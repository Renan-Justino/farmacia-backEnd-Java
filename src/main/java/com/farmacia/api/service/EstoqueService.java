package com.farmacia.api.service;

import com.farmacia.api.exception.business.EstoqueInsuficienteException;
import com.farmacia.api.model.Medicamento;
import com.farmacia.api.model.MovimentacaoEstoque;
import com.farmacia.api.model.enums.TipoMovimentacao;
import com.farmacia.api.repository.MovimentacaoEstoqueRepository;
import com.farmacia.api.web.estoque.dto.MovimentacaoRequestDTO;
import com.farmacia.api.web.estoque.dto.MovimentacaoResponseDTO; // Import necessário
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
    public List<MovimentacaoResponseDTO> listarPorMedicamento(Long medicamentoId) {
        medicamentoService.buscarEntidadePorId(medicamentoId);

        // Converte para DTO para evitar o erro de proxy/no session do Jackson
        return repository.findByMedicamentoIdOrderByDataHoraDesc(medicamentoId)
                .stream()
                .map(m -> new MovimentacaoResponseDTO(
                        m.getId(),
                        m.getMedicamento().getNome(),
                        m.getTipo().name(),
                        m.getQuantidade(),
                        m.getObservacao(),
                        m.getDataHora()
                )).toList();
    }

    @Transactional
    public void registrarEntrada(MovimentacaoRequestDTO dto) {
        processarMovimentacao(dto, TipoMovimentacao.ENTRADA);
    }

    @Transactional
    public void registrarSaida(MovimentacaoRequestDTO dto) {
        processarMovimentacao(dto, TipoMovimentacao.SAIDA);
    }

    @Transactional
    public void salvarMovimentacaoInterna(Medicamento m, TipoMovimentacao tipo, MovimentacaoRequestDTO dto) {
        salvarLogMovimentacao(m, tipo, dto);
    }

    private void processarMovimentacao(MovimentacaoRequestDTO dto, TipoMovimentacao tipo) {
        Medicamento medicamento = medicamentoService.buscarEntidadePorId(dto.getMedicamentoId());

        if (tipo == TipoMovimentacao.SAIDA) {
            if (medicamento.getQuantidadeEstoque() < dto.getQuantidade()) {
                throw new EstoqueInsuficienteException(medicamento.getNome());
            }
            medicamento.setQuantidadeEstoque(medicamento.getQuantidadeEstoque() - dto.getQuantidade());
        } else {
            medicamento.setQuantidadeEstoque(medicamento.getQuantidadeEstoque() + dto.getQuantidade());
        }

        salvarLogMovimentacao(medicamento, tipo, dto);
    }

    private void salvarLogMovimentacao(Medicamento m, TipoMovimentacao tipo, MovimentacaoRequestDTO dto) {
        MovimentacaoEstoque mov = new MovimentacaoEstoque();
        mov.setMedicamento(m);
        mov.setTipo(tipo);
        mov.setQuantidade(dto.getQuantidade());
        mov.setObservacao(dto.getObservacao());
        repository.save(mov);
    }
}