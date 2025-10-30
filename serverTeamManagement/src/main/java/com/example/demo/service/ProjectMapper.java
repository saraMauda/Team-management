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
default Project projectDTOToProject(ProjectDTO dto){
        Project project=new Project();
        project.setProjectId(dto.getId());
        project.setProjectName(dto.getName());
        project.setProjectDescription(dto.getDescription());
        project.setProjectStartDate(dto.getStartDate());
        project.setProjectEndDate(dto.getEndDate());
        project.setProjectStatus(dto.getStatus());
        project.setProgressPercentage(dto.getProgress());

    return project;
}
}
