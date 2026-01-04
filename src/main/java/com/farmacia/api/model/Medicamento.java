package com.farmacia.api.model;

import com.farmacia.api.web.medicamento.dto.MedicamentoUpdateDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "medicamento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String descricao;

    @Column(nullable = false)
    private BigDecimal preco;

    // Quantidade atual disponível em estoque
    @Column(nullable = false)
    private Integer quantidadeEstoque;

    private LocalDate dataValidade;

    // Medicamento inativo não pode ser vendido
    @Column(nullable = false)
    private Boolean ativo = true;

    // Categoria é referência externa ao agregado Medicamento
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Categoria categoria;

    /**
     * Atualiza os dados principais do medicamento.
     * Estoque não é alterado aqui por ser responsabilidade do módulo de estoque.
     */
    public void atualizarDados(MedicamentoUpdateDTO dto, Categoria novaCategoria) {
        this.nome = dto.nome();
        this.descricao = dto.descricao();
        this.preco = dto.preco();
        this.dataValidade = dto.dataValidade();
        this.ativo = dto.ativo();
        this.categoria = novaCategoria;
    }
}
