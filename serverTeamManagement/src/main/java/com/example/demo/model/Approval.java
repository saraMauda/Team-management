package com.example.demo.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Approval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long approvalId;
    private boolean approved;
    private LocalDate approvalDate;
    @ManyToOne
    @JoinColumn(name = "employeeInProjectId")
    private EmployeeInProject approvalEmployeeInProject;
    @ManyToOne
    @JoinColumn(name = "meetingId")
    private Meeting meeting;

    public Approval() {
    }

    public Approval(Long approvalId, EmployeeInProject approvalEmployeeInProject, Meeting meeting, boolean approved, LocalDate approvalDate) {
        this.approvalId = approvalId;
        this.approvalEmployeeInProject = approvalEmployeeInProject;
        this.meeting = meeting;
        this.approved = approved;
        this.approvalDate = approvalDate;
    }

    public Long getApprovalId() {
        return approvalId;
    }

    public void setApprovalId(Long approvalId) {
        this.approvalId = approvalId;
    }

    public EmployeeInProject getApprovalEmployeeInProject() {
        return approvalEmployeeInProject;
    }

    public void setApprovalEmployeeInProject(EmployeeInProject approvalEmployeeInProject) {
        this.approvalEmployeeInProject = approvalEmployeeInProject;
    }

    public Meeting getMeeting() {
        return meeting;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }
    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public LocalDate getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(LocalDate approvalDate) {
        this.approvalDate = approvalDate;
    }
}
