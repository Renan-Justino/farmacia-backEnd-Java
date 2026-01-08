package com.farmacia.api.auth.service;

import com.farmacia.api.auth.domain.Usuario;
import com.farmacia.api.auth.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Persiste um usuário e garante que a senha esteja codificada com BCrypt.
     * COMMENTS (senior):
     * - Este serviço é propositalmente simples para bootstrapping e testes locais.
     * - Em produção, adicione validações adicionais: verificação de username único, políticas de senha,
     *   envio de e-mail de confirmação e possíveis restrições de rate limit.
     * - Inclua testes unitários e testes de integração cobrindo tentativas de registro duplicado e erros de persistência.
     */
    public Usuario salvar(Usuario usuario) {
        // Verifica existência prévia para evitar violação de unicidade no banco e retornar erro de negócio claro
        repository.findByUsername(usuario.getUsername()).ifPresent(u -> {
            throw new com.farmacia.api.exception.business.BusinessException("Nome de usuário já está em uso");
        });

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return repository.save(usuario);
    }
}
