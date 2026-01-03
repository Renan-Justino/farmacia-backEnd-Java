package com.farmacia.api.repository;

import com.farmacia.api.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface de acesso a dados para a entidade Cliente.
 * Implementa consultas derivadas para validação de restrições de integridade únicas.
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /**
     * Verifica a existência de um registro por CPF para evitar duplicidade.
     */
    boolean existsByCpf(String cpf);

    /**
     * Verifica a existência de um registro por E-mail para validação de unicidade.
     */
    boolean existsByEmail(String email);
}