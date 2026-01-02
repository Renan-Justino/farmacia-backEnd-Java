package com.farmacia.api.web.cliente.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ClienteRequestDTO {
    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(
            regexp = "\\d{11}",
            message = "CPF deve conter 11 dígitos numéricos"
    )
    private String cpf;

    @Email(message = "E-mail inválido")
    private String email;

    @NotNull(message = "Data de nascimento é obrigatória")
    private LocalDate dataNascimento;
}