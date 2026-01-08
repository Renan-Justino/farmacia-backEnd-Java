package com.farmacia.api.repository;

import com.farmacia.api.model.LogOperacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogOperacaoRepository extends JpaRepository<LogOperacao, Long> {

    List<LogOperacao> findByLevelOrderByTimestampDesc(LogOperacao.NivelLog level);

    @Query("SELECT l FROM LogOperacao l WHERE l.timestamp BETWEEN :startDate AND :endDate ORDER BY l.timestamp DESC")
    List<LogOperacao> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT l FROM LogOperacao l WHERE l.level = :level AND l.timestamp BETWEEN :startDate AND :endDate ORDER BY l.timestamp DESC")
    List<LogOperacao> findByLevelAndDateRange(
        @Param("level") LogOperacao.NivelLog level,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    List<LogOperacao> findAllByOrderByTimestampDesc();
}

