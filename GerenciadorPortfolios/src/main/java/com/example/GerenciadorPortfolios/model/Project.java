package com.example.GerenciadorPortfolios.model;

import com.example.GerenciadorPortfolios.model.enums.ProjectStatus;
import com.example.GerenciadorPortfolios.model.enums.RiskLevel;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private LocalDate startDate;
    private LocalDate expectedEndDate;
    private LocalDate actualEndDate;
    private BigDecimal budget;
    private String description;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Member manager;

    @ManyToMany
    @JoinTable(
            name = "project_member",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    private Set<Member> teamMembers = new HashSet<>();

    public RiskLevel calculateRiskLevel() {
        long monthsDuration = java.time.temporal.ChronoUnit.MONTHS.between(
                startDate,
                expectedEndDate
        );

        if (budget.compareTo(new BigDecimal("100000")) <= 0 && monthsDuration <= 3) {
            return RiskLevel.LOW;
        } else if ((budget.compareTo(new BigDecimal("100000")) > 0 &&
                budget.compareTo(new BigDecimal("500000")) <= 0) ||
                (monthsDuration > 3 && monthsDuration <= 6)) {
            return RiskLevel.MEDIUM;
        } else {
            return RiskLevel.HIGH;
        }
    }

    public boolean canBeDeleted() {
        return !(status == ProjectStatus.STARTED ||
                status == ProjectStatus.IN_PROGRESS ||
                status == ProjectStatus.FINISHED);
    }

    public boolean isValidStatusTransition(ProjectStatus newStatus) {
        if (newStatus == ProjectStatus.CANCELLED) {
            return true;
        }

        return newStatus.ordinal() == this.status.ordinal() + 1;
    }
}