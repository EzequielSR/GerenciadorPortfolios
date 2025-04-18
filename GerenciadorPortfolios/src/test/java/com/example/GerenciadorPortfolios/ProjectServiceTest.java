package com.example.GerenciadorPortfolios;

import com.example.GerenciadorPortfolios.dto.ProjectDto;
import com.example.GerenciadorPortfolios.exception.*;
import com.example.GerenciadorPortfolios.mapper.ProjectMapper;
import com.example.GerenciadorPortfolios.model.Member;
import com.example.GerenciadorPortfolios.model.Project;
import com.example.GerenciadorPortfolios.model.enums.ProjectStatus;
import com.example.GerenciadorPortfolios.model.enums.RiskLevel;
import com.example.GerenciadorPortfolios.repository.MemberRepository;
import com.example.GerenciadorPortfolios.repository.ProjectRepository;
import com.example.GerenciadorPortfolios.service.ExternalMemberService;
import com.example.GerenciadorPortfolios.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ExternalMemberService externalMemberService;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectService projectService;

    private Project project;
    private ProjectDto projectDto;
    private Member manager;
    private Member teamMember;

    @BeforeEach
    void setUp() {
        manager = new Member();
        manager.setId(1L);
        manager.setName("Manager");
        manager.setRole("gerente");

        teamMember = new Member();
        teamMember.setId(2L);
        teamMember.setName("Team Member");
        teamMember.setRole("funcionario");

        project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setStartDate(LocalDate.now());
        project.setExpectedEndDate(LocalDate.now().plusMonths(2));
        project.setBudget(new BigDecimal("50000"));
        project.setDescription("Test Description");
        project.setStatus(ProjectStatus.UNDER_ANALYSIS);
        project.setManager(manager);
        project.setTeamMembers(new HashSet<>(Collections.singletonList(teamMember)));

        projectDto = new ProjectDto();
        projectDto.setId(1L);
        projectDto.setName("Test Project");
        projectDto.setStartDate(LocalDate.now());
        projectDto.setExpectedEndDate(LocalDate.now().plusMonths(2));
        projectDto.setBudget(new BigDecimal("50000"));
        projectDto.setDescription("Test Description");
        projectDto.setStatus(ProjectStatus.UNDER_ANALYSIS);
        projectDto.setManagerId(1L);
        projectDto.setTeamMemberIds(new HashSet<>(Collections.singleton(2L)));
    }

    @Test
    void createProject_ValidData_ReturnsProjectDto() throws InvalidDateException, InvalidMemberRoleException, MemberNotFoundException, InvalidTeamException {
        when(projectMapper.toEntity(any(ProjectDto.class))).thenReturn(project);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(manager));
        when(memberRepository.findAllById(any())).thenReturn(Collections.singletonList(teamMember));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.toDto(any(Project.class))).thenReturn(projectDto);

        ProjectDto result = projectService.createProject(projectDto);

        assertNotNull(result);
        assertEquals(projectDto.getName(), result.getName());
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void createProject_InvalidManagerRole_ThrowsException() {
        manager.setRole("funcionario");
        when(memberRepository.findById(1L)).thenReturn(Optional.of(manager));

        assertThrows(InvalidMemberRoleException.class, () -> {
            projectService.createProject(projectDto);
        });
    }

    @Test
    void updateProjectStatus_ValidTransition_UpdatesStatus() throws ProjectNotFoundException, InvalidStatusTransitionException {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.toDto(any(Project.class))).thenReturn(projectDto);

        ProjectDto result = projectService.updateProjectStatus(1L, ProjectStatus.ANALYSIS_DONE);

        assertNotNull(result);
        assertEquals(ProjectStatus.ANALYSIS_DONE, project.getStatus());
    }

    @Test
    void updateProjectStatus_InvalidTransition_ThrowsException() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThrows(InvalidStatusTransitionException.class, () -> {
            projectService.updateProjectStatus(1L, ProjectStatus.IN_PROGRESS);
        });
    }

    @Test
    void deleteProject_WhenStatusAllows_DeletesProject() throws ProjectNotFoundException, ProjectDeletionException {
        project.setStatus(ProjectStatus.UNDER_ANALYSIS);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        projectService.deleteProject(1L);

        verify(projectRepository, times(1)).delete(project);
    }

    @Test
    void deleteProject_WhenStatusNotAllowed_ThrowsException() {
        project.setStatus(ProjectStatus.STARTED);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThrows(ProjectDeletionException.class, () -> {
            projectService.deleteProject(1L);
        });
    }

    @Test
    void calculateRiskLevel_LowRisk_ReturnsLow() {
        project.setBudget(new BigDecimal("100000"));
        project.setExpectedEndDate(LocalDate.now().plusMonths(3));

        RiskLevel riskLevel = project.calculateRiskLevel();

        assertEquals(RiskLevel.LOW, riskLevel);
    }

    @Test
    void calculateRiskLevel_MediumRiskByBudget_ReturnsMedium() {
        project.setBudget(new BigDecimal("300000"));
        project.setExpectedEndDate(LocalDate.now().plusMonths(2));

        RiskLevel riskLevel = project.calculateRiskLevel();

        assertEquals(RiskLevel.MEDIUM, riskLevel);
    }

    @Test
    void calculateRiskLevel_HighRiskByDuration_ReturnsHigh() {
        project.setBudget(new BigDecimal("100000"));
        project.setExpectedEndDate(LocalDate.now().plusMonths(7));

        RiskLevel riskLevel = project.calculateRiskLevel();

        assertEquals(RiskLevel.HIGH, riskLevel);
    }

    @Test
    void generatePortfolioReport_ReturnsCorrectData() {
        List<Project> projects = Collections.singletonList(project);
        when(projectRepository.findAll()).thenReturn(projects);
        when(projectRepository.countByStatus(any(ProjectStatus.class))).thenReturn(1L);
        when(projectRepository.sumBudgetByStatus(any(ProjectStatus.class))).thenReturn(new BigDecimal("50000"));

        Map<String, Object> report = projectService.generatePortfolioReport();

        assertNotNull(report);
        assertTrue(report.containsKey("projectsByStatus"));
        assertTrue(report.containsKey("budgetByStatus"));
        assertTrue(report.containsKey("averageDurationDays"));
        assertTrue(report.containsKey("uniqueMembersAllocated"));
    }
}