package com.farmacia.api.model;

import com.farmacia.api.exception.business.EstoqueInsuficienteException;
import com.farmacia.api.exception.business.MedicamentoInativoException;
import com.farmacia.api.exception.business.MedicamentoVencidoException;
import com.farmacia.api.web.medicamento.dto.MedicamentoUpdateDTO;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
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

    @Column(nullable = false)
    private Integer quantidadeEstoque;

    private LocalDate dataValidade;

    @Column(nullable = false)
    private Boolean ativo = true;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Categoria categoria;

    public void validarDisponibilidadeParaVenda(int quantidade) {
        if (!Boolean.TRUE.equals(ativo)) throw new MedicamentoInativoException();
        if (dataValidade != null && dataValidade.isBefore(LocalDate.now())) throw new MedicamentoVencidoException();
        if (quantidade <= 0) throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        if (quantidadeEstoque < quantidade) throw new EstoqueInsuficienteException(nome);
    }

    public void baixarEstoque(int quantidade) {
        validarDisponibilidadeParaVenda(quantidade);
        quantidadeEstoque -= quantidade;
    }

    public void atualizarDados(MedicamentoUpdateDTO dto, Categoria novaCategoria) {
        this.nome = dto.nome();
        this.descricao = dto.descricao();
        this.preco = dto.preco();
        this.dataValidade = dto.dataValidade();
        this.ativo = dto.ativo();
        this.categoria = novaCategoria;
    }

    public void alterarStatus(Boolean status) {
        this.ativo = status;
    }
}
