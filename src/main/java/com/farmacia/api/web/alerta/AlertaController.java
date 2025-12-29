package com.farmacia.api.web.alerta;

import com.farmacia.api.service.AlertaService;
import com.farmacia.api.web.medicamento.dto.MedicamentoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/alertas")
@RequiredArgsConstructor
public class AlertaController {

    private final AlertaService alertaService;

    @GetMapping("/estoque-baixo")
    public ResponseEntity<List<MedicamentoResponseDTO>> getEstoqueBaixo() {
        return ResponseEntity.ok(alertaService.listarEstoqueBaixo());
    }

    @GetMapping("/validade-proxima")
    public ResponseEntity<List<MedicamentoResponseDTO>> getValidadeProxima() {
        return ResponseEntity.ok(alertaService.listarValidadeProxima());
    }
}