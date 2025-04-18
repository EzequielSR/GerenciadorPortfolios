package com.example.GerenciadorPortfolios.repository;

import com.example.GerenciadorPortfolios.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // Métodos básicos de busca
    List<Member> findByRole(String role);

    // Método para buscar vários membros de uma vez
    List<Member> findAllByIdIn(Set<Long> ids);

    // Métodos para relatórios e validações
    @Query("SELECT COUNT(DISTINCT m) FROM Member m JOIN m.projects p")
    long countUniqueMembersInProjects();

    @Query("SELECT COUNT(DISTINCT m) FROM Project p JOIN p.teamMembers m WHERE p.id IN :projectIds")
    long countUniqueMembersInSpecificProjects(@Param("projectIds") List<Long> projectIds);

    // Método para verificar se membros existem
    @Query("SELECT COUNT(m) FROM Member m WHERE m.id IN :memberIds")
    long countByIds(@Param("memberIds") Set<Long> memberIds);

    // Método para buscar membros por projeto
    @Query("SELECT m FROM Member m JOIN m.projects p WHERE p.id = :projectId")
    List<Member> findByProjectId(@Param("projectId") Long projectId);
}