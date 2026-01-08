package com.farmacia.api.auth.web;

import com.farmacia.api.auth.web.dto.UsuarioResponseDTO;
import com.farmacia.api.auth.web.dto.LoginRequestDTO;
import com.farmacia.api.auth.web.dto.LoginResponseDTO;
import com.farmacia.api.infra.security.JwtService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth")
@lombok.extern.slf4j.Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final com.farmacia.api.auth.service.UsuarioService usuarioService;
    private final com.farmacia.api.service.LogService logService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          com.farmacia.api.auth.service.UsuarioService usuarioService,
                          com.farmacia.api.service.LogService logService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
        this.logService = logService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.username(),
                                    request.password()
                            )
                    );

            UserDetails user = (UserDetails) authentication.getPrincipal();
            String token = jwtService.gerarToken(user);

            logService.registrarLog(
                com.farmacia.api.model.LogOperacao.NivelLog.INFO,
                String.format("Login realizado: %s", request.username()),
                "AUTH",
                "LOGIN",
                "USUARIO",
                null,
                "Usuário autenticado com sucesso"
            );

            return ResponseEntity.ok(new LoginResponseDTO(token));
        } catch (org.springframework.security.core.AuthenticationException ex) {
            // Credenciais inválidas ou problema na autenticação
            log.warn("Authentication failed for user {}: {}", request.username(), ex.getMessage());
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).build();
        } catch (Exception ex) {
            log.error("Unexpected error during login for {}: {}", request.username(), ex.getMessage(), ex);
            throw ex; // GlobalExceptionHandler irá transformar em 500 e registrar o stacktrace
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuário", description = "Registra um novo usuário com perfil padrão ATENDENTE, codifica a senha e persiste.")
    public ResponseEntity<UsuarioResponseDTO> register(@RequestBody @jakarta.validation.Valid com.farmacia.api.auth.web.dto.RegisterRequestDTO request) {
        // Mapear DTO para domínio
        com.farmacia.api.auth.domain.Usuario usuario = new com.farmacia.api.auth.domain.Usuario();
        usuario.setUsername(request.username());
        usuario.setPassword(request.password());
        usuario.setPerfil(request.perfil() != null ? request.perfil() : com.farmacia.api.auth.domain.Perfil.ATENDENTE);
        usuario.setAtivo(true);

        try {
            // Persistir via service (que já trata hash de senha)
            com.farmacia.api.auth.domain.Usuario salvo = usuarioService.salvar(usuario);

            // Não retornar senha de forma alguma. Comentário sênior: em produção, voltar apenas dados necessários e link de confirmação.
            com.farmacia.api.auth.web.dto.UsuarioResponseDTO response = new com.farmacia.api.auth.web.dto.UsuarioResponseDTO(
                    salvo.getId(),
                    salvo.getUsername(),
                    salvo.getPerfil().name(),
                    salvo.isAtivo()
            );

            logService.registrarLog(
                com.farmacia.api.model.LogOperacao.NivelLog.INFO,
                String.format("Usuário criado: %s (Perfil: %s)", salvo.getUsername(), salvo.getPerfil().name()),
                "AUTH",
                "CREATE",
                "USUARIO",
                salvo.getId(),
                String.format("Username: %s, Perfil: %s, Ativo: %s", 
                    salvo.getUsername(), salvo.getPerfil().name(), salvo.isAtivo())
            );

            return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(response);
        } catch (com.farmacia.api.exception.business.BusinessException ex) {
            log.warn("Failed to register user {}: {}", request.username(), ex.getMessage());
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY).build();
        } catch (Exception ex) {
            log.error("Unexpected error during register for {}: {}", request.username(), ex.getMessage(), ex);
            throw ex; // handled globally
        }
    }
}
