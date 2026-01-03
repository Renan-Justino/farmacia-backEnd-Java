package com.farmacia.api.repository;

import com.farmacia.api.model.MovimentacaoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // ADICIONE ESTE IMPORT
import org.springframework.data.repository.query.Param; // ADICIONE ESTE IMPORT
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {

    @Query("SELECT m FROM MovimentacaoEstoque m " +
            "JOIN FETCH m.medicamento med " +
            "JOIN FETCH med.categoria " +
            "WHERE med.id = :medicamentoId " +
            "ORDER BY m.dataHora DESC")
    List<MovimentacaoEstoque> findByMedicamentoIdOrderByDataHoraDesc(@Param("medicamentoId") Long medicamentoId);
}