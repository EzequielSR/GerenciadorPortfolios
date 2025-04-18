package com.example.GerenciadorPortfolios.repository;

import com.example.GerenciadorPortfolios.model.Project;
import com.example.GerenciadorPortfolios.model.enums.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // Métodos básicos de filtro
    Page<Project> findByStatus(ProjectStatus status, Pageable pageable);

    @Query("SELECT p FROM Project p WHERE " +
            "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:status IS NULL OR p.status = :status)")
    Page<Project> findWithFilters(
            @Param("name") String name,
            @Param("status") ProjectStatus status,
            Pageable pageable);

    // Métodos para relatórios
    @Query("SELECT COUNT(p) FROM Project p WHERE p.status = :status")
    long countByStatus(@Param("status") ProjectStatus status);

    @Query("SELECT COALESCE(SUM(p.budget), 0) FROM Project p WHERE p.status = :status")
    BigDecimal sumBudgetByStatus(@Param("status") ProjectStatus status);

    // Métodos para validação de membros
    @Query("SELECT COUNT(p) FROM Project p JOIN p.teamMembers m WHERE m.id = :memberId " +
            "AND p.status NOT IN :excludedStatuses")
    long countActiveProjectsByMember(
            @Param("memberId") Long memberId,
            @Param("excludedStatuses") List<ProjectStatus> excludedStatuses);

    // Método alternativo para busca por nome e status
    Page<Project> findByNameContainingIgnoreCaseAndStatus(
            String name,
            ProjectStatus status,
            Pageable pageable);

    // Método para encontrar projetos finalizados
    List<Project> findByStatus(ProjectStatus status);
}