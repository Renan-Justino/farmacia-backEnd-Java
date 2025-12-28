package com.farmacia.api.controller;

import com.farmacia.api.dto.medicamento.MedicamentoRequestDTO;
import com.farmacia.api.dto.medicamento.MedicamentoResponseDTO;
import com.farmacia.api.service.MedicamentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicamentos")
public class MedicamentoController {

    private final MedicamentoService medicamentoService;

    public MedicamentoController(MedicamentoService medicamentoService) {
        this.medicamentoService = medicamentoService;
    }

    // 🔹 REFATORADO: recebe DTO
    @PostMapping
    public ResponseEntity<MedicamentoResponseDTO> cadastrar(
            @RequestBody MedicamentoRequestDTO request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(medicamentoService.cadastrar(request));
    }

    // 🔹 REFATORADO
    @GetMapping
    public ResponseEntity<List<MedicamentoResponseDTO>> listar() {
        return ResponseEntity.ok(medicamentoService.listarTodos());
    }

    // 🔹 REFATORADO
    @GetMapping("/{id}")
    public ResponseEntity<MedicamentoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(medicamentoService.buscarPorId(id));
    }

    // 🔹 REFATORADO
    @PutMapping("/{id}")
    public ResponseEntity<MedicamentoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody MedicamentoRequestDTO request) {

        return ResponseEntity.ok(medicamentoService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        medicamentoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
