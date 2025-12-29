package com.farmacia.api.web.estoque;

import com.farmacia.api.model.MovimentacaoEstoque;
import com.farmacia.api.service.EstoqueService;
import com.farmacia.api.web.estoque.dto.MovimentacaoRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estoque")
@RequiredArgsConstructor
public class EstoqueController {

    private final EstoqueService estoqueService;

    @PostMapping("/entrada")
    public ResponseEntity<Void> entrada(@RequestBody @Valid MovimentacaoRequestDTO request) {
        estoqueService.registrarEntrada(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/saida")
    public ResponseEntity<Void> saida(@RequestBody @Valid MovimentacaoRequestDTO request) {
        estoqueService.registrarSaida(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{medicamentoId}")
    public ResponseEntity<List<MovimentacaoEstoque>> obterHistorico(@PathVariable Long medicamentoId) {
        List<MovimentacaoEstoque> historico = estoqueService.listarPorMedicamento(medicamentoId);
        return ResponseEntity.ok(historico);
    }
}