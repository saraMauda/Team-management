package com.example.demo.dto;

import com.example.demo.model.EmployeeInProject;
import com.example.demo.model.Project;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.List;

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


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<EmployeeInProject> getEmployeeProjects() {
        return employeeProjects;
    }

    public void setEmployeeProjects(List<EmployeeInProject> employeeProjects) {
        this.employeeProjects = employeeProjects;
    }

    public List<Project> getLeaderProjects() {
        return leaderProjects;
    }

    public void setLeaderProjects(List<Project> leaderProjects) {
        this.leaderProjects = leaderProjects;
    }
}
