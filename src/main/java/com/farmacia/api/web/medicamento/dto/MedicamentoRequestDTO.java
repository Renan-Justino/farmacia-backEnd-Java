package com.farmacia.api.web.medicamento.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicamentoRequestDTO {

    @NotBlank(message = "O nome do medicamento é obrigatório")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
    private String nome;

    private String descricao;

    @NotNull(message = "O preço é obrigatório")
    @DecimalMin(value = "0.01", message = "O preço deve ser no mínimo 0.01")
    @Positive(message = "O preço deve ser maior que zero")
    private BigDecimal preco;

    @NotNull(message = "A quantidade em estoque é obrigatória")
    @PositiveOrZero(message = "O estoque não pode ser negativo")
    private Integer quantidadeEstoque;

    @NotNull(message = "A data de validade é obrigatória")
    @Future(message = "A data de validade deve ser uma data futura")
    private LocalDate dataValidade;

    // Removemos o valor default true aqui para validar a regra de negócio no Service
    private Boolean ativo;

    @NotNull(message = "A categoria é obrigatória")
    private Long categoriaId;
}