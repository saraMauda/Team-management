package com.example.demo.dto;

import com.example.demo.model.EmployeeInProject;
import com.example.demo.model.Project;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class UsersDTO {
    private Long id;
    private String name;
    private String email;
    private String password;
    private String role;
    private boolean active;
    private String image;
    private List<EmployeeInProject> employeeProjects;
    private List<Project> leaderProjects;
}
