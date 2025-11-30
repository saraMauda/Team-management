package com.example.demo.controller;

import com.example.demo.model.EmployeeInProject;
import com.example.demo.model.Project;
import com.example.demo.model.Users;
import com.example.demo.service.EmployeeInProjectRepository;
import com.example.demo.service.ProjectRepository;
import com.example.demo.service.UsersRepository;
import javax.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employeeProject")
public class EmployeeInProjectController {

    @Autowired
    private EmployeeInProjectRepository employeeInProjectRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @PostMapping("/assign")
    @PreAuthorize("hasAnyRole('TEAMLEADER','ADMIN')")
    public String assignEmployeeToProject(@RequestParam @Min(1) Long userId,
                                          @RequestParam @Min(1) Long projectId) {

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        EmployeeInProject eip = new EmployeeInProject();
        eip.setUser(user);
        eip.setProject(project);
        eip.setAssignedDate(LocalDate.now());
        eip.setStatus("ACTIVE");

        employeeInProjectRepository.save(eip);

        return "Employee assigned successfully";
    }

    @GetMapping("/leader/{leaderId}/employees")
    @PreAuthorize("hasAnyRole('TEAMLEADER','ADMIN')")
    public List<Users> getEmployeesForLeader(@PathVariable Long leaderId) {

        usersRepository.findById(leaderId)
                .orElseThrow(() -> new RuntimeException("Leader not found"));

        return employeeInProjectRepository.findByProject_Leader_Id(leaderId)
                .stream()
                .map(EmployeeInProject::getUser)
                .distinct()
                .collect(Collectors.toList());
    }
}
