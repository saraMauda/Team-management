package com.example.demo.service;

import com.example.demo.dto.ProjectDTO;
import com.example.demo.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(target = "id", source = "projectId")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "endDate", source = "endDate")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "progress", source = "progressPercentage")
    @Mapping(target = "location", source = "location")

    @Mapping(target = "leaderId", source = "leader.id")
    @Mapping(target = "leaderName", source = "leader.name")

    @Mapping(
            target = "employeeIds",
            expression = "java(project.getEmployeeProjects().stream()" +
                    ".filter(e -> \"ACTIVE\".equals(e.getStatus()))" +
                    ".map(e -> e.getUser().getId())" +
                    ".toList())"
    )
    ProjectDTO projectToProjectDTO(Project project);
}
