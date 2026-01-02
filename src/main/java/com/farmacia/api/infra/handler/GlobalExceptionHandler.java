package com.farmacia.api.infra.handler;

import com.farmacia.api.exception.ResourceNotFoundException;
import com.farmacia.api.exception.business.BusinessException;
import com.farmacia.api.infra.handler.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Controller Advice centralizado para tratamento de exceções cross-cutting.
 * Segue o padrão de design 'Global Exception Handling' para desacoplar o fluxo de erro da lógica de negócio.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Captura falhas de busca de recursos (Ex: GET /vendas/{id} inexistente).
     * @return 404 Not Found - Indica que o identificador fornecido não corresponde a nenhum registro.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "Recurso Não Encontrado", ex.getMessage(), request);
    }

    /**
     * Intercepta violações de regras de negócio (Ex: Venda de medicamento vencido ou estoque insuficiente).
     * @return 422 Unprocessable Entity - Utilizado quando a sintaxe está correta, mas a semântica da instrução viola regras de domínio.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex, HttpServletRequest request) {
        // Alinhado ao buildResponse para manter consistência na estrutura de saída
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, "Violação de Regra de Negócio", ex.getMessage(), request);
    }

    /**
     * Trata erros de validação de Bean Validation (@Valid).
     * Transforma a stack de erros do Spring em uma mensagem legível para o cliente da API.
     * @return 400 Bad Request - Erro de contrato/entrada de dados.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String mensagemFormatada = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("[%s]: %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining(" | "));

        return buildResponse(HttpStatus.BAD_REQUEST, "Erro de Validação de Dados", mensagemFormatada, request);
    }

    /**
     * Fallback para qualquer exceção não mapeada explicitamente.
     * Evita o vazamento de stacktraces sensíveis para o cliente (Information Exposure).
     * @return 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        // Log de erro com stacktrace completo apenas no servidor para depuração.
        log.error("Unhandled Exception em {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro Crítico Interno",
                "Ocorreu um erro inesperado. O suporte técnico foi notificado.",
                request);
    }

    /**
     * Factory Method privado para padronização do corpo da resposta de erro.
     * Garante que toda a API fale a mesma língua em cenários de falha.
     */
    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String error, String message, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                error,
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(response);
    }
}