package com.farmacia.api.web.venda;

import com.farmacia.api.service.VendaService;
import com.farmacia.api.web.venda.dto.VendaRequestDTO;
import com.farmacia.api.web.venda.dto.VendaResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendas")
@RequiredArgsConstructor
public class VendaController {

    private final VendaService vendaService;

    @PostMapping
    public ResponseEntity<VendaResponseDTO> registrar(@RequestBody @Valid VendaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vendaService.registrarVenda(request));
    }

    // RESOLVE O PROBLEMA: Agora o método listarPorCliente é utilizado
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<VendaResponseDTO>> listarPorCliente(@PathVariable Long clienteId) {
        List<VendaResponseDTO> vendas = vendaService.listarPorCliente(clienteId);
        return ResponseEntity.ok(vendas);
    }

    @GetMapping
    public ResponseEntity<List<VendaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(vendaService.listarTodas());
    }
}