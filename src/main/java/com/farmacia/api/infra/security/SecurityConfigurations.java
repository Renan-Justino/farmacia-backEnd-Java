package com.farmacia.api.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuração central de segurança da aplicação.
 *
 * - Desativa CSRF (API stateless)
 * - Define política stateless (JWT)
 * - Libera rotas públicas (auth, swagger)
 * - Protege todas as demais rotas
 * - Registra o filtro JWT
 */
@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfigurations(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // API REST → sem sessão
                .csrf(csrf -> csrf.disable())

                // JWT → STATELESS
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Autorização de rotas
                .authorizeHttpRequests(auth -> auth
                        // Rotas públicas
                        .requestMatchers(
                                "/auth/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/favicon.ico"
                        ).permitAll()

                        // Qualquer outra rota precisa de token
                        .anyRequest().authenticated()
                )

                // Filtro JWT antes do filtro padrão de login
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    /**
     * AuthenticationManager usado no AuthController (/auth/login)
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
