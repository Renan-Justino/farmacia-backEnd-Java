package com.farmacia.api.exception.business;

public class MedicamentoInativoException extends BusinessException {
    public MedicamentoInativoException() {
        super("O medicamento está inativo e não pode ser vendido.");
    }
}