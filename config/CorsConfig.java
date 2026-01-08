package com.farmacia.api.infra.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        /*
         * DEVELOPMENT NOTE (squad):
         * - Para facilitar o desenvolvimento local e o uso do Vite proxy, permitimos aqui padrões de origem.
         * - Em ambiente de produção, substituir por uma lista explícita de origins (ex.: https://app.exemplo.com)
         *   e remover o curinga ou usar propriedades para injetar dinamicamente as origins permitidas.
         */
        config.setAllowedOriginPatterns(List.of("*")); // dev-friendly, revert before deploy to production

        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        // Permitir todos os headers para evitar bloqueios por headers customizados no dev
        config.setAllowedHeaders(List.of("*"));

        // Expor cabeçalhos se necessário
        config.setExposedHeaders(List.of("Authorization"));

        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
