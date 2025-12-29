package com.farmacia.api.web.cliente.dto;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ClienteResponseDTO {
    private Long id;
    private String nome;
    private String cpf;
    private String email;
    private LocalDate dataNascimento;
}