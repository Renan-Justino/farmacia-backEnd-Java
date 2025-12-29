package com.farmacia.api.web.cliente.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ClienteRequestDTO {
    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "CPF é obrigatório")
    private String cpf; // Aqui você pode usar @CPF se tiver a dependência do Hibernate Validator

    @Email(message = "E-mail inválido")
    private String email;

    @NotNull(message = "Data de nascimento é obrigatória")
    private LocalDate dataNascimento;
}