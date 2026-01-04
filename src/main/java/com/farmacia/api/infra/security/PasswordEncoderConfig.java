package com.farmacia.api.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {

    /**
     * Bean de PasswordEncoder utilizando BCrypt.
     * BCrypt é adaptativo e resistente a ataques de força bruta.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Strength 12 recomendado para ambientes corporativos, ajustar conforme necessidade
        return new BCryptPasswordEncoder(12);
    }
}
