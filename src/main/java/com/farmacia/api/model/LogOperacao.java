package com.farmacia.api.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "log_operacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class LogOperacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private NivelLog level;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(length = 100)
    private String module;

    @Column(length = 100)
    private String action;

    @Column(length = 50)
    private String username;

    private Long userId;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(length = 50)
    private String entityType; // CLIENTE, MEDICAMENTO, ESTOQUE, VENDA, USUARIO, CATEGORIA

    private Long entityId;

    public enum NivelLog {
        INFO, WARN, ERROR, DEBUG
    }
}

