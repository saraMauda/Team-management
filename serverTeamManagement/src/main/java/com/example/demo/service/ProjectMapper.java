package com.example.demo.service;

import com.example.demo.dto.ProjectDTO;
import com.example.demo.model.Project;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    List<ProjectDTO> projectsToProjectDTOs(List<Project> projects);

    default ProjectDTO projectToProjectDTO(Project project) {
        ProjectDTO dto = new ProjectDTO();
        dto.setId(project.getProjectId());
        dto.setName(project.getProjectName());
        dto.setDescription(project.getProjectDescription());
        dto.setStartDate(project.getProjectStartDate());
        dto.setEndDate(project.getProjectEndDate());
        dto.setStatus(project.getProjectStatus());
        dto.setProgress(project.getProgressPercentage());

        if (project.getProjectLeader() != null)
            dto.setLeaderName(project.getProjectLeader().getName());

        if (project.getProjectCategory() != null)
            dto.setCategoryName(project.getProjectCategory().getCategoryName());

        return dto;
    }

    // ✅ חשוב מאוד: לוודא שגם מזהה וגם progress נשמרים
    default Project projectDTOToProject(ProjectDTO dto) {
        Project p = new Project();
        if (dto.getId() != null) {
            p.setProjectId(dto.getId());
        }
        p.setProjectName(dto.getName());
        p.setProjectDescription(dto.getDescription());
        p.setProjectStartDate(dto.getStartDate());
        p.setProjectEndDate(dto.getEndDate());
        p.setProjectStatus(dto.getStatus());
        p.setProgressPercentage(dto.getProgress());
        return p;
    }
}
