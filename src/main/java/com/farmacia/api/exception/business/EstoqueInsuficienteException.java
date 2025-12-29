package com.farmacia.api.exception.business;

public class EstoqueInsuficienteException extends BusinessException {
    public EstoqueInsuficienteException(String nomeMedicamento) {
        super("Estoque insuficiente para o medicamento: " + nomeMedicamento);
    }
}