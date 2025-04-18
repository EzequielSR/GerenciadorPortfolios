package com.example.GerenciadorPortfolios.controller;

import com.example.GerenciadorPortfolios.dto.ProjectDto;
import com.example.GerenciadorPortfolios.exception.*;
import com.example.GerenciadorPortfolios.model.enums.ProjectStatus;
import com.example.GerenciadorPortfolios.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "API for managing projects")
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping
    @Operation(summary = "Get all projects with pagination")
    public ResponseEntity<Page<ProjectDto>> getAllProjects(Pageable pageable) {
        return ResponseEntity.ok(projectService.findAllProjects(pageable));
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter projects by name and/or status")
    public ResponseEntity<Page<ProjectDto>> filterProjects(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) ProjectStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(projectService.findProjectsWithFilters(name, status, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a project by ID")
    public ResponseEntity<ProjectDto> getProjectById(@PathVariable Long id) throws ProjectNotFoundException {
        return ResponseEntity.ok(projectService.findProjectById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new project")
    public ResponseEntity<ProjectDto> createProject(@RequestBody ProjectDto projectDto) throws InvalidDateException, InvalidMemberRoleException, MemberNotFoundException, InvalidTeamException {
        return ResponseEntity.ok(projectService.createProject(projectDto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing project")
    public ResponseEntity<ProjectDto> updateProject(
            @PathVariable Long id,
            @RequestBody ProjectDto projectDto) throws ProjectNotFoundException, InvalidDateException, InvalidMemberRoleException, MemberNotFoundException, InvalidTeamException {
        return ResponseEntity.ok(projectService.updateProject(id, projectDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a project")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) throws ProjectNotFoundException, ProjectDeletionException {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update project status")
    public ResponseEntity<ProjectDto> updateProjectStatus(
            @PathVariable Long id,
            @RequestParam ProjectStatus status) throws ProjectNotFoundException, InvalidStatusTransitionException {
        return ResponseEntity.ok(projectService.updateProjectStatus(id, status));
    }

    @GetMapping("/report")
    @Operation(summary = "Generate portfolio report")
    public ResponseEntity<Map<String, Object>> generateReport() {
        return ResponseEntity.ok(projectService.generatePortfolioReport());
    }
}