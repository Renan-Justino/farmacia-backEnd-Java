package com.farmacia.api.model;

import com.farmacia.api.model.enums.TipoMovimentacao;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id") // Histórico imutável, identidade baseada no ID
public class MovimentacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Medicamento associado à movimentação
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicamento_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Medicamento medicamento;

    // Define se a movimentação é entrada ou saída
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimentacao tipo;

    // Quantidade movimentada (valor absoluto)
    @Column(nullable = false)
    private Integer quantidade;

    // Observação livre para auditoria e rastreabilidade
    private String observacao;

    // Timestamp da movimentação (registrado no momento da criação)
    private LocalDateTime dataHora = LocalDateTime.now();
}
