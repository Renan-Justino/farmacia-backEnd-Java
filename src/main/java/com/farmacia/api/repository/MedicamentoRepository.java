package com.farmacia.api.repository;

import com.farmacia.api.model.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByCategoriaId(Long categoriaId);
}