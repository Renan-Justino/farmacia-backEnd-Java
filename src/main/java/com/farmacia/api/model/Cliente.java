package com.farmacia.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id") // Igualdade baseada apenas no identificador persistente
@RequiredArgsConstructor // Cria construtor apenas para campos @NonNull
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false)
    @NotBlank(message = "Nome não pode ser vazio")
    private String nome;

    @NonNull
    @Column(unique = true, nullable = false, length = 11)
    @NotBlank(message = "CPF não pode ser vazio")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos numéricos")
    private String cpf;

    @NonNull
    @Column(unique = true, nullable = false)
    @NotBlank(message = "Email não pode ser vazio")
    @Email(message = "Email deve ser válido")
    private String email;

    @NonNull
    @Column(nullable = false)
    @Past(message = "Data de nascimento deve ser no passado")
    private LocalDate dataNascimento;

    // Soft-state: cliente inativo não deve participar de novas vendas
    @Column(nullable = false)
    private boolean ativo = true;
}
