package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
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
    @JsonIgnore
    private Users leader;

    @OneToMany(mappedBy = "project")
    @JsonIgnore
    private List<EmployeeInProject> employeeProjects = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    @JsonIgnore
    private List<Meeting> meetings;

    public Project(Long projectId, String name, String description, LocalDate startDate, LocalDate endDate, String status, int progressPercentage, String location, Users leader, List<EmployeeInProject> employeeProjects, List<Meeting> meetings) {
        this.projectId = projectId;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.progressPercentage = progressPercentage;
        this.location = location;
        this.leader = leader;
        this.employeeProjects = employeeProjects;
        this.meetings = meetings;
    }

    public Project() {
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(int progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Users getLeader() {
        return leader;
    }

    public void setLeader(Users leader) {
        this.leader = leader;
    }

    public List<EmployeeInProject> getEmployeeProjects() {
        return employeeProjects;
    }

    public void setEmployeeProjects(List<EmployeeInProject> employeeProjects) {
        this.employeeProjects = employeeProjects;
    }

    public List<Meeting> getMeetings() {
        return meetings;
    }

    public void setMeetings(List<Meeting> meetings) {
        this.meetings = meetings;
    }
}
