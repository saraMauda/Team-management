package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Users{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;
//    private String role;
    private boolean active;
    private String imagePath;
    @OneToMany(mappedBy="user")
    private List<EmployeeInProject> employeeProjects;
    @OneToMany(mappedBy="projectLeader")
    private List<Project> leaderProjects;


    @ManyToMany
    @JsonIgnore
    private Set<Role> roles=new HashSet<>();
    @Transient
    private String roleString;

    public Users() {

    }

    public Users(Long id, String name, String email, String password,  boolean active, String imagePath, List<EmployeeInProject> employeeProjects, List<Project> leaderProjects) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.active = active;
        this.imagePath = imagePath;
        this.employeeProjects = employeeProjects;
        this.leaderProjects = leaderProjects;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

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
    public String getRoleString() {
        return roleString;
    }

    public void setRoleString(String roleString) {
        this.roleString = roleString;
    }
}

