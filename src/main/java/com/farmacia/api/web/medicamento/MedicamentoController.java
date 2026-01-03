package com.farmacia.api.web.medicamento;

import com.farmacia.api.service.MedicamentoService;
import com.farmacia.api.web.medicamento.dto.MedicamentoRequestDTO;
import com.farmacia.api.web.medicamento.dto.MedicamentoResponseDTO;
import com.farmacia.api.web.medicamento.dto.MedicamentoUpdateDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicamentos")
@RequiredArgsConstructor // Substitui o construtor manual anterior
@Tag(name = "Medicamento")
public class MedicamentoController {

    private final MedicamentoService medicamentoService;

    @PostMapping
    @Operation(summary = "Registar medicamento", description = "Adiciona um novo medicamento ao catálogo.")
    public ResponseEntity<MedicamentoResponseDTO> cadastrar(@RequestBody @Valid MedicamentoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medicamentoService.cadastrar(dto));
    }

    @GetMapping
    @Operation(summary = "Listar medicamentos", description = "Lista todos os medicamentos disponíveis.")
    public ResponseEntity<List<MedicamentoResponseDTO>> listar() {
        return ResponseEntity.ok(medicamentoService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter medicamento por ID", description = "Recupera informações técnicas de um medicamento.")
    public ResponseEntity<MedicamentoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(medicamentoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar medicamento", description = "Altera dados cadastrais. O estoque não pode ser alterado por aqui.")
    public ResponseEntity<MedicamentoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid MedicamentoUpdateDTO dto) {
        return ResponseEntity.ok(medicamentoService.atualizar(id, dto));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Alterar status", description = "Ativa ou desativa um medicamento.")
    public ResponseEntity<Void> alterarStatus(@PathVariable Long id, @RequestBody Boolean ativo) {
        medicamentoService.alterarStatus(id, ativo);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir medicamento", description = "Remove um medicamento do sistema.")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        medicamentoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}