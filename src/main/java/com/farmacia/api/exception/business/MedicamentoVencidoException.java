package com.farmacia.api.exception.business;

public class MedicamentoVencidoException extends BusinessException {
    public MedicamentoVencidoException() {
        super("Operação negada: O medicamento está com a data de validade vencida.");
    }
}