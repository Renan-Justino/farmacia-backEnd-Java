package com.farmacia.api.repository;

import com.farmacia.api.model.MovimentacaoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {
    // Busca o histórico de um medicamento específico, do mais recente para o mais antigo
    List<MovimentacaoEstoque> findByMedicamentoIdOrderByDataHoraDesc(Long medicamentoId);
}