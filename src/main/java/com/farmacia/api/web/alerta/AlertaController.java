package com.farmacia.api.web.alerta;

import com.farmacia.api.service.AlertaService;
import com.farmacia.api.web.medicamento.dto.MedicamentoResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/alertas")
@RequiredArgsConstructor
@Tag(name = "Alerta")
public class AlertaController {

    private final AlertaService alertaService;

    @GetMapping("/estoque-baixo")
    @Operation(
            summary = "Alertas de estoque",
            description = "Identifica medicamentos com quantidade abaixo ou igual ao limite informado.\n" +
                    "Caso deixe esse campo em branco, será assumido o valor de 10 unidades."
    )
    public ResponseEntity<List<MedicamentoResponseDTO>> getEstoqueBaixo(
            @RequestParam(defaultValue = "10") int limite) {

        return ResponseEntity.ok(alertaService.listarEstoqueBaixo(limite));
    }

    @GetMapping("/validade-proxima")
    @Operation(
            summary = "Alertas de validade",
            description = "Lista medicamentos que vencerão nos próximos X dias.\n" +
                    "Caso deixe esse campo em branco, será assumido o valor de 30 dias."
    )
    public ResponseEntity<List<MedicamentoResponseDTO>> getValidadeProxima(
            @RequestParam(defaultValue = "30") int dias) {

        return ResponseEntity.ok(alertaService.listarValidadeProxima(dias));
    }
}
