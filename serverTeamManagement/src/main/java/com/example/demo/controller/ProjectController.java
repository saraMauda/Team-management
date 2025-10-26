package com.example.demo.controller;

import com.example.demo.dto.ProjectDTO;
import com.example.demo.model.Project;
import com.example.demo.service.ProjectMapper;
import com.example.demo.service.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMapper projectMapper;

    @GetMapping
    public List<ProjectDTO> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(projectMapper::projectToProjectDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ProjectDTO getProject(@PathVariable Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        return projectMapper.projectToProjectDTO(project);
    }
}
