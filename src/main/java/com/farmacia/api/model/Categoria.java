package com.farmacia.api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id") // Igualdade baseada apenas no identificador persistente
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nome funcional da categoria (ex: Analgésicos, Antibióticos)
    @Column(nullable = false)
    private String nome;

    // Descrição livre para uso administrativo
    @Column(nullable = false)
    private String descricao;
}
