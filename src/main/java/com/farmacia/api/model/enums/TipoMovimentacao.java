package com.farmacia.api.model.enums;

/**
 * Enum que representa o tipo de movimentação de estoque.
 * Usado para diferenciar entradas e saídas e manter histórico auditável.
 */
public enum TipoMovimentacao {

    /** Movimento de entrada no estoque (ex.: compra, devolução) */
    ENTRADA,

    /** Movimento de saída do estoque (ex.: venda, perda, expiração) */
    SAIDA
}
