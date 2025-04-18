package com.example.GerenciadorPortfolios.repository;

import com.example.GerenciadorPortfolios.model.Membro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface MembroRepository extends JpaRepository<Membro, Long> {

    List<Membro> findByAtribuicao(String atribuicao); // Alterado de 'role' para 'atribuicao'

    // Método para buscar vários membros de uma vez
    List<Membro> findAllByIdIn(Set<Long> ids);

    // Métodos para relatórios e validações
    @Query("SELECT COUNT(DISTINCT m) FROM Membro m JOIN m.projetos p") // Corrigido para usar 'Membro' e 'projetos'
    long countUniqueMembersInProjects();

    @Query("SELECT COUNT(DISTINCT m) FROM Projeto p JOIN p.membros m WHERE p.id IN :projectIds") // Corrigido para usar 'membros'
    long countUniqueMembersInSpecificProjects(@Param("projectIds") List<Long> projectIds);

    // Método para verificar se membros existem
    @Query("SELECT COUNT(m) FROM Membro m WHERE m.id IN :memberIds") // Corrigido para usar 'Membro'
    long countByIds(@Param("memberIds") Set<Long> memberIds);

    // Método para buscar membros por projeto
    @Query("SELECT m FROM Membro m JOIN m.projetos p WHERE p.id = :projectId") // Corrigido para usar 'projetos'
    List<Membro> findByProjectId(@Param("projectId") Long projectId);
}