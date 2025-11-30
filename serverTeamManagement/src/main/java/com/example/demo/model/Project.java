package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private int progressPercentage;
    private String location;
    @ManyToOne
    @JoinColumn(name = "userId")
    private Users leader;

    @OneToMany(mappedBy = "project")
    private List<EmployeeInProject> employeeProjects = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    private List<Meeting> meetings;

    public Project() {}
}
