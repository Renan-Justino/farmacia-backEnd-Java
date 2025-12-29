package com.farmacia.api.auth.web.dto;

public record LoginRequestDTO(
        String username,
        String password
) {}
