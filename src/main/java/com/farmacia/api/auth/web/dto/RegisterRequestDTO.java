package com.farmacia.api.auth.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.farmacia.api.auth.domain.Perfil;

/**
 * DTO para registro de novos usuários.
 * Observações (squad):
 * - Campos mínimos para criação são username e password. Perfil é opcional e defaultará para ATENDENTE.
 * - Validações simples foram adicionadas aqui; regras mais complexas (senha forte, verificação de disponibilidade) devem estar
 *   em camadas de serviço e com testes automatizados.
 */
public record RegisterRequestDTO(
        @NotBlank
        @Size(min = 3, max = 50)
        String username,

        @NotBlank
        @Size(min = 6, max = 100)
        String password,

        /** Opcional: se não informado, usa ATENDENTE. */
        Perfil perfil
) {}
