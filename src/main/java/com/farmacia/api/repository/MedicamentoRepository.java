package com.farmacia.api.repository;

import com.farmacia.api.model.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByCategoriaId(Long categoriaId);

    // Medicamentos ativos com estoque baixo
    List<Medicamento> findByAtivoTrueAndQuantidadeEstoqueLessThanEqual(Integer quantidade);

    // Medicamentos ativos com validade próxima
    List<Medicamento> findByAtivoTrueAndDataValidadeBetween(
            LocalDate inicio,
            LocalDate fim
    );
}
