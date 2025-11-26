package com.example.demo.service;

import com.example.demo.dto.ProjectDTO;
import com.example.demo.model.EmployeeInProject;
import com.example.demo.model.Project;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    default ProjectDTO projectToProjectDTO(Project project) {
        ProjectDTO dto = new ProjectDTO();

        dto.setId(project.getProjectId());
        dto.setName(project.getProjectName());
        dto.setDescription(project.getProjectDescription());
        dto.setStartDate(project.getProjectStartDate());
        dto.setEndDate(project.getProjectEndDate());
        dto.setStatus(project.getProjectStatus());
        dto.setProgress(project.getProgressPercentage());
        dto.setLocation(project.getProjectLocation());

        // ⭐ מנהל פרויקט
        if (project.getProjectLeader() != null) {
            dto.setLeaderId(project.getProjectLeader().getId());
            dto.setLeaderName(project.getProjectLeader().getName());
        }

        // ⭐ עובדים ACTIVE בלבד
        dto.setEmployeeIds(
                project.getProjectEmployeeProjects()
                        .stream()
                        .filter(e -> "ACTIVE".equals(e.getStatus()))
                        .map(e -> e.getUser().getId())
                        .toList()
        );

        return dto;
    }
}
