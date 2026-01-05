package com.farmacia.api.repository;

import com.farmacia.api.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    boolean existsByNomeIgnoreCase(String nome);

    // Método necessário para checar unicidade na atualização
    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);
}
