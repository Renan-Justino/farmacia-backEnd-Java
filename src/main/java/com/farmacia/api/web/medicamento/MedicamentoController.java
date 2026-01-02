package com.farmacia.api.web.medicamento;

import com.farmacia.api.web.medicamento.dto.MedicamentoRequestDTO;
import com.farmacia.api.web.medicamento.dto.MedicamentoResponseDTO;
import com.farmacia.api.service.MedicamentoService;
import jakarta.validation.Valid;
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

    @PostMapping
    public ResponseEntity<MedicamentoResponseDTO> cadastrar(
            @RequestBody @Valid MedicamentoRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(medicamentoService.cadastrar(dto));
    }

    @GetMapping
    public ResponseEntity<List<MedicamentoResponseDTO>> listar() {
        return ResponseEntity.ok(medicamentoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicamentoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(medicamentoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicamentoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid MedicamentoRequestDTO dto) {

        return ResponseEntity.ok(medicamentoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        medicamentoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> alterarStatus(@PathVariable Long id, @RequestBody Boolean ativo) {
        medicamentoService.alterarStatus(id, ativo);
        return ResponseEntity.noContent().build();
    }
}
