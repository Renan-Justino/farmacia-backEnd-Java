package com.farmacia.api.infra.security;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "jwt")
public final class JwtProperties {

    private final String secret;
    private final Long expiration;

    public JwtProperties(String secret, Long expiration) {
        this.secret = secret;
        this.expiration = expiration;
    }
}
