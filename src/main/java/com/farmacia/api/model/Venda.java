package com.farmacia.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Timestamp da venda, definido no serviço para garantir consistência de timezone
    private LocalDateTime dataVenda;

    private BigDecimal valorTotal;

    // Venda é dependente de Cliente, mas Cliente é agregado independente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // Venda é Aggregate Root: itens são persistidos e removidos em cascata
    @OneToMany(
            mappedBy = "venda",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ItemVenda> itens = new ArrayList<>();
}
