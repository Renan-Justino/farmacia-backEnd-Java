package com.farmacia.api.web.alerta;

import com.farmacia.api.service.AlertaService;
import com.farmacia.api.web.medicamento.dto.MedicamentoResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/alertas")
@RequiredArgsConstructor
@Tag(name = "Alerta")
public class AlertaController {

    private final AlertaService alertaService;

    @GetMapping("/estoque-baixo")
    @Operation(summary = "Alertas de estoque", description = "Identifica medicamentos com quantidade abaixo do limite de segurança.")
    public ResponseEntity<List<MedicamentoResponseDTO>> getEstoqueBaixo() {
        return ResponseEntity.ok(alertaService.listarEstoqueBaixo());
    }

    @GetMapping("/validade-proxima")
    @Operation(summary = "Alertas de validade", description = "Lista medicamentos que vencerão nos próximos 30 dias.")
    public ResponseEntity<List<MedicamentoResponseDTO>> getValidadeProxima() {
        return ResponseEntity.ok(alertaService.listarValidadeProxima());
    }
}