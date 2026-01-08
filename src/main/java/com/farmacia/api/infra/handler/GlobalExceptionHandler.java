package com.farmacia.api.infra.handler;

import com.farmacia.api.exception.ResourceNotFoundException;
import com.farmacia.api.exception.business.BusinessException;
import com.farmacia.api.infra.handler.dto.ErrorResponse;
import com.farmacia.api.service.LogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final LogService logService;

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        // Registra tentativa de acesso a recurso não encontrado
        logService.registrarLog(
            com.farmacia.api.model.LogOperacao.NivelLog.WARN,
            String.format("Tentativa de acesso a recurso não encontrado: %s", ex.getMessage()),
            extractModule(request.getRequestURI()),
            "NOT_FOUND",
            extractEntityType(request.getRequestURI()),
            extractEntityId(request.getRequestURI()),
            String.format("URI: %s | Mensagem: %s", request.getRequestURI(), ex.getMessage())
        );

        return buildResponse(HttpStatus.NOT_FOUND, "Recurso Não Encontrado", ex.getMessage(), request);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex, HttpServletRequest request) {
        // Determina a ação baseada no método HTTP
        String action = extractAction(request.getMethod(), request.getRequestURI());
        
        // Registra violação de regra de negócio
        logService.registrarLog(
            com.farmacia.api.model.LogOperacao.NivelLog.WARN,
            String.format("Tentativa de %s bloqueada: %s", action, ex.getMessage()),
            extractModule(request.getRequestURI()),
            action + "_FAILED",
            extractEntityType(request.getRequestURI()),
            extractEntityId(request.getRequestURI()),
            String.format("Método: %s | URI: %s | Regra violada: %s", 
                request.getMethod(), 
                request.getRequestURI(), 
                ex.getMessage())
        );

        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, "Violação de Regra de Negócio", ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> String.format("[%s]: %s", e.getField(), e.getDefaultMessage()))
                .collect(Collectors.joining(" | "));

        // Registra erro de validação
        String camposInvalidos = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField())
                .collect(Collectors.joining(", "));

        String action = extractAction(request.getMethod(), request.getRequestURI());

        logService.registrarLog(
            com.farmacia.api.model.LogOperacao.NivelLog.WARN,
            String.format("Tentativa de %s com dados inválidos: campos - %s", action, camposInvalidos),
            extractModule(request.getRequestURI()),
            action + "_VALIDATION_ERROR",
            extractEntityType(request.getRequestURI()),
            null,
            String.format("Método: %s | URI: %s | Campos inválidos: %s | Erros: %s", 
                request.getMethod(),
                request.getRequestURI(), 
                camposInvalidos, 
                mensagem)
        );

        return buildResponse(HttpStatus.BAD_REQUEST, "Erro de Validação", mensagem, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        // Registra erro interno do servidor
        logService.registrarLog(
            com.farmacia.api.model.LogOperacao.NivelLog.ERROR,
            String.format("Erro interno do servidor: %s", ex.getMessage()),
            extractModule(request.getRequestURI()),
            "INTERNAL_ERROR",
            null,
            null,
            String.format("URI: %s | Tipo: %s | Mensagem: %s", 
                request.getRequestURI(), 
                ex.getClass().getSimpleName(), 
                ex.getMessage())
        );

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro Interno",
                "Ocorreu um erro inesperado.",
                request
        );
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status, String error, String message, HttpServletRequest request) {

        return ResponseEntity.status(status).body(
                new ErrorResponse(
                        LocalDateTime.now(),
                        status.value(),
                        error,
                        message,
                        request.getRequestURI()
                )
        );
    }

    /**
     * Extrai o módulo da URI (ex: /api/clientes -> CLIENTE)
     */
    private String extractModule(String uri) {
        if (uri == null) return "UNKNOWN";
        
        if (uri.contains("/api/clientes")) return "CLIENTE";
        if (uri.contains("/api/medicamentos")) return "MEDICAMENTO";
        if (uri.contains("/api/estoque")) return "ESTOQUE";
        if (uri.contains("/api/vendas")) return "VENDA";
        if (uri.contains("/api/categorias")) return "CATEGORIA";
        if (uri.contains("/api/auth") || uri.contains("/auth")) return "AUTH";
        if (uri.contains("/api/logs")) return "LOG";
        
        return "SYSTEM";
    }

    /**
     * Extrai o tipo de entidade da URI
     */
    private String extractEntityType(String uri) {
        return extractModule(uri);
    }

    /**
     * Tenta extrair o ID da entidade da URI (ex: /api/clientes/123 -> 123)
     */
    private Long extractEntityId(String uri) {
        if (uri == null) return null;
        
        try {
            // Procura por padrão /api/entidade/ID
            String[] parts = uri.split("/");
            for (int i = 0; i < parts.length - 1; i++) {
                if (parts[i].matches("clientes|medicamentos|vendas|categorias|estoque") && 
                    i + 1 < parts.length) {
                    String idStr = parts[i + 1];
                    // Remove query params se houver
                    if (idStr.contains("?")) {
                        idStr = idStr.substring(0, idStr.indexOf("?"));
                    }
                    return Long.parseLong(idStr);
                }
            }
        } catch (NumberFormatException e) {
            // Não é um ID numérico, retorna null
        }
        
        return null;
    }

    /**
     * Extrai a ação baseada no método HTTP e URI
     */
    private String extractAction(String method, String uri) {
        if (method == null) return "UNKNOWN";
        
        // Mapeia método HTTP para ação
        switch (method.toUpperCase()) {
            case "POST":
                // POST em /api/entidade -> CREATE
                // POST em /api/entidade/{id}/... -> ação específica
                if (uri != null && uri.matches(".*/\\d+.*")) {
                    return "CREATE"; // Pode ser uma sub-ação, mas geralmente é CREATE
                }
                return "CREATE";
            case "PUT":
            case "PATCH":
                return "UPDATE";
            case "DELETE":
                return "DELETE";
            case "GET":
                return "READ";
            default:
                return method.toUpperCase();
        }
    }
}
