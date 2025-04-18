package com.example.GerenciadorPortfolios.dto;

import com.example.GerenciadorPortfolios.model.enums.ProjectStatus;
import com.example.GerenciadorPortfolios.model.enums.RiskLevel;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {
    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate expectedEndDate;
    private LocalDate actualEndDate;
    private BigDecimal budget;
    private String description;
    private ProjectStatus status;
    private Long managerId;
    private Set<Long> teamMemberIds;
    private RiskLevel riskLevel;
}