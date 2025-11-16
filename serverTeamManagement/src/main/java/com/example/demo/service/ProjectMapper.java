package com.example.demo.service;

import com.example.demo.dto.ProjectDTO;
import com.example.demo.model.EmployeeInProject;
import com.example.demo.model.Project;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    List<ProjectDTO> projectsToProjectDTOs(List<Project> projects);

    default ProjectDTO projectToProjectDTO(Project project) {
        if (project == null) return null;

        ProjectDTO dto = new ProjectDTO();

        dto.setId(project.getProjectId());
        dto.setName(project.getProjectName());
        dto.setDescription(project.getProjectDescription());
        dto.setStartDate(project.getProjectStartDate());
        dto.setEndDate(project.getProjectEndDate());
        dto.setStatus(project.getProjectStatus());
        dto.setProgress(project.getProgressPercentage());
        dto.setLocation(project.getProjectLocation());
        dto.setCategoryId(project.getProjectCategory() != null ? project.getProjectCategory().getCategoryId() : null);
        dto.setCategoryName(project.getProjectCategory() != null ? project.getProjectCategory().getCategoryName() : null);

        if (project.getProjectLeader() != null) {
            dto.setLeaderId(project.getProjectLeader().getId());
            dto.setLeaderName(project.getProjectLeader().getName());
        }

        if (project.getProjectEmployeeProjects() != null) {
            List<Long> employeeIds = project.getProjectEmployeeProjects()
                    .stream()
                    .map(e -> e.getUser().getId())
                    .collect(Collectors.toList());
            dto.setEmployeeIds(employeeIds);
        }

        return dto;
    }
}
