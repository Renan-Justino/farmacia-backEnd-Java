package com.farmacia.api.auth.web.dto;

/**
 * DTO de resposta para evitar exposição de senha em respostas HTTP.
 * Comentário sênior: sempre evitar MFA (mostrar senha) em responses; retornar apenas dados necessários.
 */
public record UsuarioResponseDTO(
        Long id,
        String username,
        String perfil,
        boolean ativo
) {}
