package com.farmacia.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id") // Entidade JPA deve ter igualdade baseada apenas no identificador
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    // CPF é tratado como identificador de negócio único
    @Column(unique = true, nullable = false, length = 11)
    private String cpf;

    @Column(unique = true, nullable = false)
    private String email;

    // Usado para validações de regra de negócio (ex: maioridade)
    @Column(nullable = false)
    private LocalDate dataNascimento;

    // Soft-state: cliente inativo não deve participar de novas vendas
    @Column(nullable = false)
    private Boolean ativo = true;
}
