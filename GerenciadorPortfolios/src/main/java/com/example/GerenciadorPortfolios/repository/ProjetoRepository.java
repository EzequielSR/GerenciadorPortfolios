package com.example.GerenciadorPortfolios.repository;

import com.example.GerenciadorPortfolios.model.Projeto;
import com.example.GerenciadorPortfolios.model.enums.StatusProjeto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Long> {

    Page<Projeto> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    List<Projeto> findByStatusIn(List<StatusProjeto> status);

    @Query("SELECT p.status as status, COUNT(p) as quantidade FROM Projeto p GROUP BY p.status")
    List<Map<String, Object>> countProjetosByStatus();

    @Query("SELECT p.status as status, SUM(p.orcamentoTotal) as total FROM Projeto p GROUP BY p.status")
    List<Map<String, Object>> sumOrcamentoByStatus();

    @Query(value = "SELECT AVG(EXTRACT(DAY FROM (p.data_real_termino - p.data_inicio))) " +
            "FROM projetos p " +
            "WHERE p.status = 'ENCERRADO' AND p.data_real_termino IS NOT NULL",
            nativeQuery = true)
    Double calcularMediaDuracaoProjetosEncerrados();

    @Query("SELECT COUNT(DISTINCT m.id) FROM Projeto p JOIN p.membros m")
    Long countMembrosUnicosAlocados();

    @Query("SELECT COUNT(p) FROM Projeto p JOIN p.membros m WHERE m.id = :membroId AND p.status NOT IN ('ENCERRADO', 'CANCELADO')")
    Long countProjetosAtivosPorMembro(Long membroId);
}