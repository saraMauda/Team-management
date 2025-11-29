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

    // ----- getters & setters -----

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getName() { return name; }
    public void setName(String projectName) { this.name = projectName; }

    public String getDescription() { return description; }
    public void setDescription(String projectDescription) { this.description = projectDescription; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate projectStartDate) { this.startDate = projectStartDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate projectEndDate) { this.endDate = projectEndDate; }

    public String getStatus() { return status; }
    public void setStatus(String projectStatus) { this.status = projectStatus; }

    public int getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(int progressPercentage) { this.progressPercentage = progressPercentage; }

    public String getLocation() { return location; }
    public void setLocation(String projectLocation) { this.location = projectLocation; }

    public Users getLeader() { return leader; }
    public void setLeader(Users projectLeader) { this.leader = projectLeader; }

    public List<EmployeeInProject> getEmployeeProjects() { return employeeProjects; }
    public void setEmployeeProjects(List<EmployeeInProject> projectEmployeeProjects) { this.employeeProjects = projectEmployeeProjects; }

    public List<Meeting> getMeetings() { return meetings; }
    public void setMeetings(List<Meeting> projectMeetings) { this.meetings = projectMeetings; }
}
