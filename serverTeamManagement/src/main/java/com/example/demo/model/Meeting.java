package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
@Entity
@Getter
@Setter
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long meetingId;
    private String title;
    private LocalDate meetingDate;
    private String description;
    private String meetingLocation;
    private String status;
    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name = "projectId")
    private Project project;
    @OneToMany(mappedBy = "meeting")
    private List<Approval> approvals;

    public Meeting() {
    }

    public Meeting(Long meetingId, String title, LocalDate meetingDate, String description, String meetingLocation, String status, LocalDateTime createdAt, Project project, List<Approval> approvals) {
        this.meetingId = meetingId;
        this.title = title;
        this.meetingDate = meetingDate;
        this.description = description;
        this.meetingLocation = meetingLocation;
        this.status = status;
        this.createdAt = createdAt;
        this.project = project;
        this.approvals = approvals;
    }
}
