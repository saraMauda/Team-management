package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class Users{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;
    private boolean active;
    private String imagePath;
    @OneToMany(mappedBy="user")
    private List<EmployeeInProject> employeeProjects;
    @OneToMany(mappedBy= "leader")
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
}

