package com.farmacia.api.web.estoque;

import com.farmacia.api.model.MovimentacaoEstoque;
import com.farmacia.api.service.EstoqueService;
import com.farmacia.api.web.estoque.dto.MovimentacaoRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estoque")
@RequiredArgsConstructor
@Tag(name = "Estoque")
public class EstoqueController {

    private final EstoqueService estoqueService;

    @PostMapping("/entrada")
    @Operation(summary = "Registrar entrada", description = "Adiciona saldo ao estoque de um medicamento.")
    public ResponseEntity<Void> entrada(@RequestBody @Valid MovimentacaoRequestDTO request) {
        estoqueService.registrarEntrada(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/saida")
    @Operation(summary = "Registrar saída", description = "Remove saldo do estoque (ajuste manual ou perda).")
    public ResponseEntity<Void> saida(@RequestBody @Valid MovimentacaoRequestDTO request) {
        estoqueService.registrarSaida(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{medicamentoId}")
    @Operation(summary = "Histórico de movimentação", description = "Lista todas as entradas e saídas de um medicamento específico.")
    public ResponseEntity<List<MovimentacaoEstoque>> obterHistorico(@PathVariable Long medicamentoId) {
        return ResponseEntity.ok(estoqueService.listarPorMedicamento(medicamentoId));
    }
}