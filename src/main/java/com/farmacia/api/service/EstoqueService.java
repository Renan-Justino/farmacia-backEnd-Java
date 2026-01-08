package com.farmacia.api.service;

import com.farmacia.api.model.Medicamento;
import com.farmacia.api.model.MovimentacaoEstoque;
import com.farmacia.api.model.enums.TipoMovimentacao;
import com.farmacia.api.repository.MovimentacaoEstoqueRepository;
import com.farmacia.api.web.estoque.dto.MovimentacaoRequestDTO;
import com.farmacia.api.web.estoque.dto.MovimentacaoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final MovimentacaoEstoqueRepository repository;
    private final MedicamentoService medicamentoService;
    private final LogService logService;

    @Transactional(readOnly = true)
    public List<MovimentacaoResponseDTO> listarPorMedicamento(Long medicamentoId) {
        Medicamento medicamento = medicamentoService.buscarEntidadePorId(medicamentoId);
        return repository.findByMedicamentoIdOrderByDataHoraDesc(medicamento.getId())
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional
    public void registrarEntrada(MovimentacaoRequestDTO dto) {
        processarMovimentacao(dto, TipoMovimentacao.ENTRADA);
    }

    @Transactional
    public void registrarSaida(MovimentacaoRequestDTO dto) {
        processarMovimentacao(dto, TipoMovimentacao.SAIDA);
    }

    private void processarMovimentacao(MovimentacaoRequestDTO dto, TipoMovimentacao tipo) {
        Medicamento medicamento = medicamentoService.buscarEntidadePorId(dto.getMedicamentoId());
        int quantidade = dto.getQuantidade();
        int estoqueAnterior = medicamento.getQuantidadeEstoque();

        if (tipo == TipoMovimentacao.SAIDA) {
            medicamento.baixarEstoque(quantidade); // método encapsulado na entidade
        } else {
            adicionarEstoque(medicamento, quantidade); // encapsula entrada de estoque
        }

        salvarLogMovimentacao(medicamento, tipo, dto);
        
        // Registrar log de auditoria
        logService.registrarLog(
            com.farmacia.api.model.LogOperacao.NivelLog.INFO,
            String.format("Movimentação de estoque: %s - %s de %d unidades do medicamento %s", 
                tipo.name(), tipo == TipoMovimentacao.ENTRADA ? "Entrada" : "Saída", 
                quantidade, medicamento.getNome()),
            "ESTOQUE",
            tipo == TipoMovimentacao.ENTRADA ? "ENTRADA" : "SAIDA",
            "ESTOQUE",
            medicamento.getId(),
            String.format("Medicamento: %s | Quantidade: %d | Estoque anterior: %d | Estoque atual: %d | Observação: %s",
                medicamento.getNome(), quantidade, estoqueAnterior, medicamento.getQuantidadeEstoque(), 
                dto.getObservacao() != null ? dto.getObservacao() : "N/A")
        );
    }

    private void adicionarEstoque(Medicamento medicamento, int quantidade) {
        if (quantidade <= 0) throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        medicamento.setQuantidadeEstoque(medicamento.getQuantidadeEstoque() + quantidade);
    }

    private void salvarLogMovimentacao(Medicamento medicamento, TipoMovimentacao tipo, MovimentacaoRequestDTO dto) {
        MovimentacaoEstoque mov = new MovimentacaoEstoque();
        mov.setMedicamento(medicamento);
        mov.setTipo(tipo);
        mov.setQuantidade(dto.getQuantidade());
        mov.setObservacao(dto.getObservacao());
        mov.setDataHora(agora());
        repository.save(mov);
    }

    private LocalDateTime agora() {
        return LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
    }

    private MovimentacaoResponseDTO toResponseDTO(MovimentacaoEstoque m) {
        return new MovimentacaoResponseDTO(
                m.getId(),
                m.getMedicamento().getNome(),
                m.getTipo().name(),
                m.getQuantidade(),
                m.getObservacao(),
                m.getDataHora()
        );
    }
}
