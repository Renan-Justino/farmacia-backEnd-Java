package com.farmacia.api.web.medicamento.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objeto de requisição para criação/atualização de medicamentos")
public class MedicamentoRequestDTO {

    @NotBlank(message = "O nome do medicamento é obrigatório")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
    @Schema(description = "Nome comercial do medicamento", example = "String")
    private String nome;

    @Schema(description = "Descrição detalhada do medicamento", example = "String")
    private String descricao;

    @NotNull(message = "O preço é obrigatório")
    @DecimalMin(value = "0.01", message = "O preço deve ser no mínimo 0.01")
    @Positive(message = "O preço deve ser maior que zero")
    @Schema(description = "Preço unitário de venda", example = "00.00")
    private BigDecimal preco;

    @NotNull(message = "A quantidade em estoque é obrigatória")
    @PositiveOrZero(message = "O estoque não pode ser negativo")
    @Schema(description = "Quantidade disponível em estoque", example = "00")
    private Integer quantidadeEstoque;

    @NotNull(message = "A data de validade é obrigatória")
    @Future(message = "A data de validade deve ser uma data futura")
    @Schema(description = "Data de expiração do lote", example = "0000-00-00")
    private LocalDate dataValidade;

    @Schema(description = "Indica se o medicamento está disponível para venda", example = "true")
    private Boolean ativo;

    @NotNull(message = "A categoria é obrigatória")
    @Schema(description = "ID da categoria cadastrada no sistema", example = "0")
    private Long categoriaId;
}