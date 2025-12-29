package com.farmacia.api.repository;

import com.farmacia.api.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {

    boolean existsByItensMedicamentoId(Long medicamentoId);

}