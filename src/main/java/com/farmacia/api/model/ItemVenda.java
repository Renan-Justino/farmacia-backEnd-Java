package com.farmacia.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ItemVenda não existe sem Venda (entidade dependente do agregado)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_id", nullable = false)
    @JsonIgnore // Evita ciclo infinito de serialização
    private Venda venda;

    // Medicamento é referência externa ao agregado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicamento_id", nullable = false)
    private Medicamento medicamento;

    private Integer quantidade;

    // Preço unitário é copiado no momento da venda para histórico imutável
    private BigDecimal precoUnitario;
}
