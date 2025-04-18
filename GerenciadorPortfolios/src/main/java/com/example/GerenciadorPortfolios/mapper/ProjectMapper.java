package com.example.GerenciadorPortfolios.mapper;

import com.example.GerenciadorPortfolios.dto.ProjectDto;
import com.example.GerenciadorPortfolios.model.Project;
import com.example.GerenciadorPortfolios.model.enums.ProjectStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);

    @Mapping(target = "riskLevel", expression = "java(project.calculateRiskLevel())")
    ProjectDto toDto(Project project);

    @Mapping(target = "status", source = "status", qualifiedByName = "defaultStatus")
    Project toEntity(ProjectDto projectDto);

    @Named("defaultStatus")
    default ProjectStatus defaultStatus(ProjectStatus status) {
        return status != null ? status : ProjectStatus.UNDER_ANALYSIS;
    }
}