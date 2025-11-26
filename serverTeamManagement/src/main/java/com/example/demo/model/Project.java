package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
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

    @ManyToOne
    @JoinColumn(name = "userId")
    private Users projectLeader;

    @OneToMany(mappedBy = "project")
    private List<EmployeeInProject> projectEmployeeProjects= new ArrayList<>();

    @OneToMany(mappedBy = "project")
    private List<Meeting> projectMeetings;

    public Project() {}

    // ----- getters & setters -----

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getProjectDescription() { return projectDescription; }
    public void setProjectDescription(String projectDescription) { this.projectDescription = projectDescription; }

    public LocalDate getProjectStartDate() { return projectStartDate; }
    public void setProjectStartDate(LocalDate projectStartDate) { this.projectStartDate = projectStartDate; }

    public LocalDate getProjectEndDate() { return projectEndDate; }
    public void setProjectEndDate(LocalDate projectEndDate) { this.projectEndDate = projectEndDate; }

    public String getProjectStatus() { return projectStatus; }
    public void setProjectStatus(String projectStatus) { this.projectStatus = projectStatus; }

    public int getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(int progressPercentage) { this.progressPercentage = progressPercentage; }

    public String getProjectLocation() { return projectLocation; }
    public void setProjectLocation(String projectLocation) { this.projectLocation = projectLocation; }

    public Users getProjectLeader() { return projectLeader; }
    public void setProjectLeader(Users projectLeader) { this.projectLeader = projectLeader; }

    public List<EmployeeInProject> getProjectEmployeeProjects() { return projectEmployeeProjects; }
    public void setProjectEmployeeProjects(List<EmployeeInProject> projectEmployeeProjects) { this.projectEmployeeProjects = projectEmployeeProjects; }

    public List<Meeting> getProjectMeetings() { return projectMeetings; }
    public void setProjectMeetings(List<Meeting> projectMeetings) { this.projectMeetings = projectMeetings; }
}
