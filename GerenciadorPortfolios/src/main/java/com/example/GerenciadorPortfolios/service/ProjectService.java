package com.example.GerenciadorPortfolios.service;

import com.example.GerenciadorPortfolios.dto.ProjectDto;
import com.example.GerenciadorPortfolios.exception.*;
import com.example.GerenciadorPortfolios.model.Member;
import com.example.GerenciadorPortfolios.model.Project;
import com.example.GerenciadorPortfolios.model.enums.ProjectStatus;
import com.example.GerenciadorPortfolios.model.enums.RiskLevel;
import com.example.GerenciadorPortfolios.repository.MemberRepository;
import com.example.GerenciadorPortfolios.repository.ProjectRepository;
import com.example.GerenciadorPortfolios.mapper.ProjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final ExternalMemberService externalMemberService;
    private final ProjectMapper projectMapper;

    @Transactional(readOnly = true)
    public Page<ProjectDto> findAllProjects(Pageable pageable) {
        return projectRepository.findAll(pageable)
                .map(projectMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ProjectDto> findProjectsWithFilters(String name, ProjectStatus status, Pageable pageable) {
        if (name == null && status == null) {
            return findAllProjects(pageable);
        }

        return projectRepository.findByNameContainingIgnoreCaseAndStatus(name, status, pageable)
                .map(projectMapper::toDto);
    }

    @Transactional(readOnly = true)
    public ProjectDto findProjectById(Long id) throws ProjectNotFoundException {
        return projectRepository.findById(id)
                .map(projectMapper::toDto)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + id));
    }

    @Transactional
    public ProjectDto createProject(ProjectDto projectDto) throws InvalidDateException, MemberNotFoundException,
            InvalidMemberRoleException, InvalidTeamException {
        validateProjectDates(projectDto);

        Project project = projectMapper.toEntity(projectDto);

        // Set manager
        if (projectDto.getManagerId() != null) {
            Member manager = memberRepository.findById(projectDto.getManagerId())
                    .orElseThrow(() -> new MemberNotFoundException("Manager not found with id: " + projectDto.getManagerId()));
            if (!"gerente".equalsIgnoreCase(manager.getRole())) {
                throw new InvalidMemberRoleException("Only members with 'gerente' role can be managers");
            }
            project.setManager(manager);
        }

        // Set team members
        if (projectDto.getTeamMemberIds() != null && !projectDto.getTeamMemberIds().isEmpty()) {
            validateTeamMembers(projectDto.getTeamMemberIds());
            Set<Member> teamMembers = new HashSet<>(memberRepository.findAllByIdIn(projectDto.getTeamMemberIds()));
            project.setTeamMembers(teamMembers);
        }

        project.setStatus(ProjectStatus.UNDER_ANALYSIS);
        Project savedProject = projectRepository.save(project);
        return projectMapper.toDto(savedProject);
    }

    @Transactional
    public ProjectDto updateProject(Long id, ProjectDto projectDto) throws ProjectNotFoundException,
            InvalidDateException, MemberNotFoundException, InvalidMemberRoleException, InvalidTeamException {
        Project existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + id));

        validateProjectDates(projectDto);

        // Update basic fields
        existingProject.setName(projectDto.getName());
        existingProject.setStartDate(projectDto.getStartDate());
        existingProject.setExpectedEndDate(projectDto.getExpectedEndDate());
        existingProject.setActualEndDate(projectDto.getActualEndDate());
        existingProject.setBudget(projectDto.getBudget());
        existingProject.setDescription(projectDto.getDescription());

        // Update manager if changed
        if (projectDto.getManagerId() != null &&
                (existingProject.getManager() == null || !existingProject.getManager().getId().equals(projectDto.getManagerId()))) {
            Member manager = memberRepository.findById(projectDto.getManagerId())
                    .orElseThrow(() -> new MemberNotFoundException("Manager not found with id: " + projectDto.getManagerId()));
            if (!"gerente".equalsIgnoreCase(manager.getRole())) {
                throw new InvalidMemberRoleException("Only members with 'gerente' role can be managers");
            }
            existingProject.setManager(manager);
        }

        // Update team members if changed
        if (projectDto.getTeamMemberIds() != null) {
            validateTeamMembers(projectDto.getTeamMemberIds());
            Set<Member> teamMembers = new HashSet<>(memberRepository.findAllByIdIn(projectDto.getTeamMemberIds()));
            existingProject.setTeamMembers(teamMembers);
        }

        Project updatedProject = projectRepository.save(existingProject);
        return projectMapper.toDto(updatedProject);
    }

    @Transactional
    public void deleteProject(Long id) throws ProjectNotFoundException, ProjectDeletionException {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + id));

        if (!project.canBeDeleted()) {
            throw new ProjectDeletionException("Cannot delete project with status: " + project.getStatus());
        }

        projectRepository.delete(project);
    }

    @Transactional
    public ProjectDto updateProjectStatus(Long id, ProjectStatus newStatus) throws ProjectNotFoundException,
            InvalidStatusTransitionException {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + id));

        if (!project.isValidStatusTransition(newStatus)) {
            throw new InvalidStatusTransitionException("Invalid status transition from " +
                    project.getStatus() + " to " + newStatus);
        }

        if (newStatus == ProjectStatus.FINISHED) {
            project.setActualEndDate(LocalDate.now());
        }

        project.setStatus(newStatus);
        Project updatedProject = projectRepository.save(project);
        return projectMapper.toDto(updatedProject);
    }

    private void validateProjectDates(ProjectDto projectDto) throws InvalidDateException {
        if (projectDto.getExpectedEndDate() != null &&
                projectDto.getStartDate() != null &&
                projectDto.getExpectedEndDate().isBefore(projectDto.getStartDate())) {
            throw new InvalidDateException("Expected end date cannot be before start date");
        }

        if (projectDto.getActualEndDate() != null &&
                projectDto.getStartDate() != null &&
                projectDto.getActualEndDate().isBefore(projectDto.getStartDate())) {
            throw new InvalidDateException("Actual end date cannot be before start date");
        }
    }

    private void validateTeamMembers(Set<Long> memberIds) throws InvalidTeamException, MemberNotFoundException,
            InvalidMemberRoleException {
        if (memberIds == null || memberIds.isEmpty()) {
            throw new InvalidTeamException("Project must have at least one team member");
        }

        if (memberIds.size() > 10) {
            throw new InvalidTeamException("Project cannot have more than 10 team members");
        }

        long foundMembers = memberRepository.countByIds(memberIds);
        if (foundMembers != memberIds.size()) {
            throw new MemberNotFoundException("One or more team members not found");
        }

        List<Member> members = memberRepository.findAllByIdIn(memberIds);
        for (Member member : members) {
            if (!"funcionario".equalsIgnoreCase(member.getRole())) {
                throw new InvalidMemberRoleException("Only members with 'funcionario' role can be team members");
            }

            // Check if member is already in 3 active projects
            long activeProjectsCount = projectRepository.countActiveProjectsByMember(
                    member.getId(),
                    Arrays.asList(ProjectStatus.FINISHED, ProjectStatus.CANCELLED)
            );
            if (activeProjectsCount >= 3) {
                throw new InvalidTeamException("Member " + member.getName() +
                        " is already assigned to 3 active projects");
            }
        }
    }

    @Transactional(readOnly = true)
    public Map<String, Object> generatePortfolioReport() {
        Map<String, Object> report = new HashMap<>();

        // Count projects by status
        Map<ProjectStatus, Long> projectsByStatus = Arrays.stream(ProjectStatus.values())
                .collect(Collectors.toMap(
                        status -> status,
                        status -> projectRepository.countByStatus(status)
                ));
        report.put("projectsByStatus", projectsByStatus);

        // Total budget by status
        Map<ProjectStatus, BigDecimal> budgetByStatus = Arrays.stream(ProjectStatus.values())
                .collect(Collectors.toMap(
                        status -> status,
                        status -> projectRepository.sumBudgetByStatus(status)
                ));
        report.put("budgetByStatus", budgetByStatus);

        // Average duration of finished projects
        List<Project> finishedProjects = projectRepository.findByStatus(ProjectStatus.FINISHED);
        double avgDuration = finishedProjects.stream()
                .filter(p -> p.getStartDate() != null && p.getActualEndDate() != null)
                .mapToLong(p -> ChronoUnit.DAYS.between(p.getStartDate(), p.getActualEndDate()))
                .average()
                .orElse(0);
        report.put("averageDurationDays", avgDuration);

        // Total unique members allocated
        long uniqueMembers = memberRepository.countUniqueMembersInProjects();
        report.put("uniqueMembersAllocated", uniqueMembers);

        return report;
    }
}