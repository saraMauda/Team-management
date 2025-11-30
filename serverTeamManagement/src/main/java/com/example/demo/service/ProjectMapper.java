package com.example.demo.service;

import com.example.demo.dto.ProjectDTO;
import com.example.demo.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(target = "id", source = "projectId")
    @Mapping(target = "leaderId", source = "leader.id")
    @Mapping(target = "leaderName", source = "leader.name")
    @Mapping(target = "progressPercentage", source = "progressPercentage")
    @Mapping(target = "employeeIds",
            expression = "java(project.getEmployeeProjects() == null ? java.util.Collections.emptyList() : " +
                    "project.getEmployeeProjects().stream()" +
                    ".map(e -> e.getUser().getId())" +
                    ".collect(java.util.stream.Collectors.toList()))"
    )
    ProjectDTO toDTO(Project project);
}
