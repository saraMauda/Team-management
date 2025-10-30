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
    //יצירת פרויקט חדש
    @PostMapping
    public ProjectDTO createProject(@RequestBody ProjectDTO projectDTO){
        Project project=projectMapper.projectDTOToProject(projectDTO);
        Project saved=projectRepository.save(project);
        return projectMapper.projectToProjectDTO(saved);
    }
    @PutMapping("/{id}")
    public ProjectDTO updateProject(@PathVariable Long id, @RequestBody ProjectDTO projectDTO){
        Project existing = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        existing.setProjectName(projectDTO.getName());
        existing.setProjectDescription(projectDTO.getDescription());
        existing.setProjectStartDate(projectDTO.getStartDate());
        existing.setProjectEndDate(projectDTO.getEndDate());

        Project updated = projectRepository.save(existing);
        return projectMapper.projectToProjectDTO(updated);
    }
    //מחיקת פרויקט
    @DeleteMapping("/{id}")
    public void deleteProject(@PathVariable Long id) {
        projectRepository.deleteById(id);
    }
    // שליפת פרויקטים לפי מנהל צוות
    @GetMapping("/byLeader/{leaderId}")
    public List<ProjectDTO> getProjectsByLeader(@PathVariable Long leaderId) {
        return projectRepository.findByProjectLeader_Id(leaderId)
                .stream()
                .map(projectMapper::projectToProjectDTO)
                .collect(Collectors.toList());
    }
    //שליפת פרויקטים לפי עובד
    @GetMapping("/byEmployee/{employeeId}")
    public List<ProjectDTO> getProjectsByEmployee(@PathVariable Long employeeId) {
        return projectRepository.findByProjectEmployeeProjects_User_Id(employeeId)
                .stream()
                .map(projectMapper::projectToProjectDTO)
                .collect(Collectors.toList());
    }

}
