package com.example.demo.controller;

import com.example.demo.model.EmployeeInProject;
import com.example.demo.model.Project;
import com.example.demo.model.Users;
import com.example.demo.service.EmployeeInProjectRepository;
import com.example.demo.service.ProjectRepository;
import com.example.demo.service.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employee-project")
public class EmployeeInProjectController {

    @Autowired
    private EmployeeInProjectRepository employeeInProjectRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @PostMapping("/assign")
    public String assignEmployeeToProject(@RequestParam Long userId,
                                          @RequestParam Long projectId) {

        Users user = usersRepository.findById(userId).orElseThrow();
        Project project = projectRepository.findById(projectId).orElseThrow();

        EmployeeInProject eip = new EmployeeInProject();
        eip.setUser(user);
        eip.setProject(project);
        eip.setAssignedDate(LocalDate.now());
        eip.setStatus("ACTIVE");

        employeeInProjectRepository.save(eip);

        return "Employee assigned successfully";
    }

    @GetMapping("/leader/{leaderId}/employees")
    public List<Users> getEmployeesForLeader(@PathVariable Long leaderId) {

        return employeeInProjectRepository.findByProject_Leader_Id(leaderId)
                .stream()
                .map(EmployeeInProject::getUser)
                .distinct()
                .collect(Collectors.toList());
    }
}
