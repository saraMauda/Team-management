package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.persistence.Id;

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
    @JoinColumn(name="projectId")
    private Project project;
    @ManyToOne
    @JoinColumn(name = "userId")
    private Users user;
    private String roleDescription;
    @OneToMany(mappedBy = "reportEmployeeInProject")
    private List<Report> reports;
    @OneToMany(mappedBy = "approvalEmployeeInProject")
    private List<Approval> approvals;

    public EmployeeInProject() {
    }

    public EmployeeInProject(Long employeeProjectId, Project project, Users user, String roleDescription, List<Report> reports, List<Approval> approvals, LocalDate assignedDate, String status) {
        this.employeeProjectId = employeeProjectId;
        this.project = project;
        this.user = user;
        this.roleDescription = roleDescription;
        this.reports = reports;
        this.approvals = approvals;
        this.assignedDate = assignedDate;
        this.status = status;
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

    public Long getEmployeeProjectId() {
        return employeeProjectId;
    }

    public void setEmployeeProjectId(Long employeeProjectId) {
        this.employeeProjectId = employeeProjectId;
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
