package com.farmacia.api.web.venda;

import com.farmacia.api.service.VendaService;
import com.farmacia.api.web.venda.dto.VendaRequestDTO;
import com.farmacia.api.web.venda.dto.VendaResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendas")
@RequiredArgsConstructor
@Tag(name = "Venda")
public class VendaController {

    private final VendaService vendaService;

    @PostMapping
    @Operation(summary = "Registrar venda", description = "Processa uma nova transação de venda e atualiza o estoque automaticamente.")
    public ResponseEntity<VendaResponseDTO> registrar(@RequestBody @Valid VendaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vendaService.registrarVenda(request));
    }

    @GetMapping
    @Operation(summary = "Listar vendas", description = "Retorna o histórico global de vendas efetuadas.")
    public ResponseEntity<List<VendaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(vendaService.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter venda por ID", description = "Exibe os detalhes e itens de uma venda específica.")
    public ResponseEntity<VendaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(vendaService.buscarPorId(id));
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar por cliente", description = "Filtra o histórico de vendas de um cliente específico.")
    public ResponseEntity<List<VendaResponseDTO>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(vendaService.listarPorCliente(clienteId));
    }
}