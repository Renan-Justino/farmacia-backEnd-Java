package com.farmacia.api.model;

import com.farmacia.api.web.medicamento.dto.MedicamentoUpdateDTO; // Import necessário
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter // Melhor que @Data para entidades JPA
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

    @Column(nullable = false)
    private Integer quantidadeEstoque;

    private LocalDate dataValidade;

    @Column(nullable = false)
    private Boolean ativo = true;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    /**
     * Método de domínio (Rich Model) para atualização de dados cadastrais.
     * Protege a integridade do estoque, permitindo alterações apenas em campos permitidos.
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