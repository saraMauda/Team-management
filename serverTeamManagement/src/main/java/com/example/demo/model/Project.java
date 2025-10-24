package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    private String projectName;
    private String projectDescription;
    private LocalDate projectStartDate;
    private LocalDate projectEndDate;
    private String projectStatus;
    private int progressPercentage;
    private String projectLocation;
    private LocalDate lastUpdated;
    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category projectCategory;
    @ManyToOne
    @JoinColumn(name = "userId")
    private Users projectLeader;
    @OneToMany(mappedBy = "project")
    private List <Meeting> projectMeetings;
    @OneToMany(mappedBy = "project")
    private List<EmployeeInProject> projectEmployeeProjects;

    public Project(Long projectId, String projectName, String projectDescription, LocalDate projectStartDate, LocalDate projectEndDate, Category projectCategory, Users projectLeader, List<Meeting> projectMeetings, List<EmployeeInProject> projectEmployeeProjects, String projectStatus, int progressPercentage, String projectLocation, LocalDate lastUpdated) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.projectStartDate = projectStartDate;
        this.projectEndDate = projectEndDate;
        this.projectCategory = projectCategory;
        this.projectLeader = projectLeader;
        this.projectMeetings = projectMeetings;
        this.projectEmployeeProjects = projectEmployeeProjects;
        this.projectStatus = projectStatus;
        this.progressPercentage = progressPercentage;
        this.projectLocation = projectLocation;
        this.lastUpdated = lastUpdated;
    }

    public Project() {
    }

    public void setProjectMeetings(List<Meeting> projectMeetings) {
        this.projectMeetings = projectMeetings;
    }

    public String getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(String projectStatus) {
        this.projectStatus = projectStatus;
    }

    public int getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(int progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public String getProjectLocation() {
        return projectLocation;
    }

    public void setProjectLocation(String projectLocation) {
        this.projectLocation = projectLocation;
    }

    public LocalDate getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDate lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public LocalDate getProjectStartDate() {
        return projectStartDate;
    }

    public void setProjectStartDate(LocalDate projectStartDate) {
        this.projectStartDate = projectStartDate;
    }

    public LocalDate getProjectEndDate() {
        return projectEndDate;
    }

    public void setProjectEndDate(LocalDate projectEndDate) {
        this.projectEndDate = projectEndDate;
    }

    public Category getProjectCategory() {
        return projectCategory;
    }

    public void setProjectCategory(Category projectCategory) {
        this.projectCategory = projectCategory;
    }

    public Users getProjectLeader() {
        return projectLeader;
    }

    public void setProjectLeader(Users projectLeader) {
        this.projectLeader = projectLeader;
    }

    public List<Meeting> getProjectMeetings() {
        return projectMeetings;
    }

    public void setProjectMeeting(List<Meeting> projectMeetings) {
        this.projectMeetings = projectMeetings;
    }

    public List<EmployeeInProject> getProjectEmployeeProjects() {
        return projectEmployeeProjects;
    }

    public void setProjectEmployeeProjects(List<EmployeeInProject> projectEmployeeProjects) {
        this.projectEmployeeProjects = projectEmployeeProjects;
    }
}
