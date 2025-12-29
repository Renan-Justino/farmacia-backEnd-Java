package com.farmacia.api.web.venda;

import com.farmacia.api.web.venda.dto.VendaRequestDTO;
import com.farmacia.api.web.venda.dto.VendaResponseDTO;
import com.farmacia.api.service.VendaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/vendas")
public class VendaController {

    private final VendaService vendaService;

    public VendaController(VendaService vendaService) {
        this.vendaService = vendaService;
    }

    @PostMapping
    public ResponseEntity<VendaResponseDTO> registrarVenda(
            @RequestBody @Valid VendaRequestDTO request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vendaService.registrarVenda(request));
    }

    @GetMapping
    public ResponseEntity<List<VendaResponseDTO>> listar() {
        return ResponseEntity.ok(vendaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(vendaService.buscarPorId(id));
    }
}
