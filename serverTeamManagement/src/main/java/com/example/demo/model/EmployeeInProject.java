package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.beans.ConstructorProperties;
import java.time.LocalDate;
import java.util.List;

@Entity
public class EmployeeInProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeProjectId;

    private LocalDate assignedDate;
    private String status;

    @ManyToOne
    @JoinColumn(name = "projectId")
    @JsonIgnore
    private Project project;

    @ManyToOne
    @JoinColumn(name = "userId")
    private Users user;

    private String roleDescription;

    @OneToMany(mappedBy = "reportEmployeeInProject")
    @JsonIgnore
    private List<Report> reports;

    @OneToMany(mappedBy = "approvalEmployeeInProject")
    @JsonIgnore
    private List<Approval> approvals;

    public EmployeeInProject() {
    }

    public EmployeeInProject(Long employeeProjectId, LocalDate assignedDate, String status, Project project, Users user, String roleDescription, List<Report> reports, List<Approval> approvals) {
        this.employeeProjectId = employeeProjectId;
        this.assignedDate = assignedDate;
        this.status = status;
        this.project = project;
        this.user = user;
        this.roleDescription = roleDescription;
        this.reports = reports;
        this.approvals = approvals;
    }

    public Long getEmployeeProjectId() {
        return employeeProjectId;
    }

    public void setEmployeeProjectId(Long employeeProjectId) {
        this.employeeProjectId = employeeProjectId;
    }

    public LocalDate getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(LocalDate assignedDate) {
        this.assignedDate = assignedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public String getRoleDescription() {
        return roleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }

    public List<Approval> getApprovals() {
        return approvals;
    }

    public void setApprovals(List<Approval> approvals) {
        this.approvals = approvals;
    }
}
