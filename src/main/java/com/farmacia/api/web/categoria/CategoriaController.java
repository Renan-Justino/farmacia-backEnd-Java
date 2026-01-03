package com.farmacia.api.web.categoria;

import com.farmacia.api.service.CategoriaService;
import com.farmacia.api.web.categoria.dto.CategoriaRequestDTO;
import com.farmacia.api.web.categoria.dto.CategoriaResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor // Substitui o construtor manual anterior
@Tag(name = "Categoria")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @PostMapping
    @Operation(summary = "Criar categoria", description = "Regista uma nova categoria de medicamentos.")
    public ResponseEntity<CategoriaResponseDTO> cadastrar(@RequestBody @Valid CategoriaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.cadastrar(request));
    }

    @GetMapping
    @Operation(summary = "Listar categorias", description = "Retorna todas as categorias cadastradas.")
    public ResponseEntity<List<CategoriaResponseDTO>> listar() {
        return ResponseEntity.ok(categoriaService.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter categoria por ID", description = "Busca detalhes de uma categoria específica.")
    public ResponseEntity<CategoriaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.buscarPorId(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir categoria", description = "Remove uma categoria do sistema.")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        categoriaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}