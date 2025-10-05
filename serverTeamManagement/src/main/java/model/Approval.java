package model;

import jakarta.persistence.*;

@Entity
public class Approval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long approvalId;
    @ManyToOne
    @JoinColumn(name = "employeeInProjectId")
    private EmployeeInProject approvalEmployeeInProject;
    @ManyToOne
    @JoinColumn(name = "meetingId")
    private Meeting meeting;

    public Approval() {
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
}
