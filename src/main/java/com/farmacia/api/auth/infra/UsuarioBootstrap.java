package com.farmacia.api.auth.infra;

import com.farmacia.api.auth.domain.Perfil;
import com.farmacia.api.auth.domain.Usuario;
import com.farmacia.api.auth.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class UsuarioBootstrap implements CommandLineRunner {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (repository.count() == 0) {
            Usuario admin = new Usuario(
                    null,
                    "admin",
                    passwordEncoder.encode("admin123"),
                    Perfil.ADMIN,
                    true
            );
            repository.save(admin);
        }
    }
}
