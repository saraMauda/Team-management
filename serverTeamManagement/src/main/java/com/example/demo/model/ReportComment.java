package com.example.demo.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
public class ReportComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne
    @JoinColumn(name = "reportId")
    private Report report;
    @ManyToOne
    @JoinColumn(name = "userId")
    private Users user;
    private String text;
    private LocalDateTime commentDate;
    private boolean isEdited;

    public ReportComment() {
    }

    public ReportComment(Long commentId, Report report, Users user, String text, LocalDateTime commentDate, boolean isEdited) {
        this.commentId = commentId;
        this.report = report;
        this.user = user;
        this.text = text;
        this.commentDate = commentDate;
        this.isEdited = isEdited;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public Long getCommentId() {
        return commentId;
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
}
