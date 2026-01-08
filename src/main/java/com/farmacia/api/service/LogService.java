package com.farmacia.api.service;

import com.farmacia.api.model.LogOperacao;
import com.farmacia.api.repository.LogOperacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogService {

    private final LogOperacaoRepository logRepository;

    @Transactional
    public void registrarLog(
        LogOperacao.NivelLog level,
        String message,
        String module,
        String action,
        String entityType,
        Long entityId,
        String details
    ) {
        try {
            String username = getCurrentUsername();
            Long userId = getCurrentUserId();

            LogOperacao logOperacao = new LogOperacao();
            logOperacao.setTimestamp(LocalDateTime.now());
            logOperacao.setLevel(level);
            logOperacao.setMessage(message);
            logOperacao.setModule(module);
            logOperacao.setAction(action);
            logOperacao.setUsername(username);
            logOperacao.setUserId(userId);
            logOperacao.setEntityType(entityType);
            logOperacao.setEntityId(entityId);
            logOperacao.setDetails(details);

            logRepository.save(logOperacao);
        } catch (Exception e) {
            // Não deve quebrar a operação principal se o log falhar
            log.error("Erro ao registrar log: {}", e.getMessage());
        }
    }

    public List<LogOperacao> listarTodos() {
        return logRepository.findAllByOrderByTimestampDesc();
    }

    public List<LogOperacao> listarPorNivel(LogOperacao.NivelLog level) {
        return logRepository.findByLevelOrderByTimestampDesc(level);
    }

    public List<LogOperacao> listarPorPeriodo(LocalDateTime startDate, LocalDateTime endDate) {
        return logRepository.findByDateRange(startDate, endDate);
    }

    public List<LogOperacao> listarPorNivelEPeriodo(LogOperacao.NivelLog level, LocalDateTime startDate, LocalDateTime endDate) {
        return logRepository.findByLevelAndDateRange(level, startDate, endDate);
    }

    private String getCurrentUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                return authentication.getName();
            }
        } catch (Exception e) {
            log.debug("Não foi possível obter username do contexto de segurança");
        }
        return "SYSTEM";
    }

    private Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
                // Se necessário, pode extrair o ID do usuário de outra forma
                return null;
            }
        } catch (Exception e) {
            log.debug("Não foi possível obter userId do contexto de segurança");
        }
        return null;
    }
}

