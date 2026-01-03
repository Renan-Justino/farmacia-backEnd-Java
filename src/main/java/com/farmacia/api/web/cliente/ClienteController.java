package com.farmacia.api.web.cliente;

import com.farmacia.api.service.ClienteService;
import com.farmacia.api.web.cliente.dto.ClienteRequestDTO;
import com.farmacia.api.web.cliente.dto.ClienteResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
@Tag(name = "Cliente")
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    @Operation(summary = "Registar cliente", description = "Cria um novo cliente validando CPF e e-mail únicos.")
    public ResponseEntity<ClienteResponseDTO> cadastrar(@RequestBody @Valid ClienteRequestDTO request, UriComponentsBuilder uriBuilder) {
        ClienteResponseDTO response = clienteService.salvar(request);
        URI uri = uriBuilder.path("/clientes/{id}").buildAndExpand(response.getId()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar clientes", description = "Retorna a listagem completa de clientes registados.")
    public ResponseEntity<List<ClienteResponseDTO>> listar() {
        return ResponseEntity.ok(clienteService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter cliente por ID", description = "Procura os detalhes de um cliente específico.")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cliente", description = "Modifica os dados de um cliente existente.")
    public ResponseEntity<ClienteResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid ClienteRequestDTO request) {
        return ResponseEntity.ok(clienteService.atualizar(id, request));
    }
}