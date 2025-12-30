package com.farmacia.api.repository;

import com.farmacia.api.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {

    // Nome do método deve refletir o campo 'dataVenda' da entidade
    List<Venda> findByClienteIdOrderByDataVendaDesc(Long clienteId);

    boolean existsByItensMedicamentoId(Long medicamentoId);
}