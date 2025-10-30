package model;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;
@Entity
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long meetingId;
    private String title;
    private Date meetingDate;
    @ManyToOne
    @JoinColumn(name = "projectId")
    private Project project;
    @OneToMany(mappedBy = "meeting")
    private List<Approval> approvals;

    public Meeting() {
    }

    public Meeting(Long meetingId, String title, Date meetingDate, Project project, List<Approval> approvals) {
        this.meetingId = meetingId;
        this.title = title;
        this.meetingDate = meetingDate;
        this.project = project;
        this.approvals = approvals;
    }

    public Long getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(Long meetingId) {
        this.meetingId = meetingId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getMeetingDate() {
        return meetingDate;
    }

    public void setMeetingDate(Date meetingDate) {
        this.meetingDate = meetingDate;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<Approval> getApprovals() {
        return approvals;
    }

    public void setApprovals(List<Approval> approvals) {
        this.approvals = approvals;
    }
}
