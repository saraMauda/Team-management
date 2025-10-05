package model;

import jakarta.persistence.*;
import jakarta.persistence.Id;

import java.util.List;

@Entity
public class EmployeeInProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long employeeProjectId;
    @ManyToOne
    @JoinColumn(name="projectId")
    private Project project;
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
    private String roleDescription;
    @OneToMany(mappedBy = "reportEmployeeInProject")
    private List<Report> reports;
    @OneToMany(mappedBy = "approvalEmployeeInProject")
    private List<Approval> approvals;

    public EmployeeInProject() {
    }

    public EmployeeInProject(long employeeProjectId, Project project, User user, String roleDescription, List<Report> reports, List<Approval> approvals) {
        this.employeeProjectId = employeeProjectId;
        this.project = project;
        this.user = user;
        this.roleDescription = roleDescription;
        this.reports = reports;
        this.approvals = approvals;
    }

    public long getEmployeeProjectId() {
        return employeeProjectId;
    }

    public void setEmployeeProjectId(long employeeProjectId) {
        this.employeeProjectId = employeeProjectId;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
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
