package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity

public class ReportComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne
    @JoinColumn(name = "reportId")
    @JsonIgnore
    private Report report;

    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonIgnore
    private Users user;

    private String text;
    private LocalDateTime commentDate;
    private boolean isEdited;

    @Transient
    private Long userId;

    @Transient
    private String authorRole;

    public ReportComment(Long commentId, Report report, Users user, String text, LocalDateTime commentDate, boolean isEdited, Long userId, String authorRole) {
        this.commentId = commentId;
        this.report = report;
        this.user = user;
        this.text = text;
        this.commentDate = commentDate;
        this.isEdited = isEdited;
        this.userId = userId;
        this.authorRole = authorRole;
    }

    public ReportComment() {
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(LocalDateTime commentDate) {
        this.commentDate = commentDate;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAuthorRole() {
        return authorRole;
    }

    public void setAuthorRole(String authorRole) {
        this.authorRole = authorRole;
    }
}
