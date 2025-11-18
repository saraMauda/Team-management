package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ReportComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    public ReportComment() {
    }

    // הקישור לדוח
    @ManyToOne
    @JoinColumn(name = "reportId")
    @JsonIgnore
    private Report report;

    // הקישור למשתמש שכתב את התגובה (חיוני לצ'אט)
    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonIgnore
    private Users user;

    private String text;
    private LocalDateTime commentDate;
    private boolean isEdited;

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
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
        return this.user != null ? this.user.getId() : null;
    }

    /**
     * מחזיר את התפקיד של המשתמש. Spring משתמש במתודה זו כדי להוסיף שדה "authorRole" ל-JSON.
     * נדרש כדי לקבוע אם התגובה היא מ-"Team Lead" או "Employee" ב-Front-end.
     */
    public String getAuthorRole() {
        // הנחה: getUsers() קיים ומחזיר את התפקיד (כגון "TEAMLEADER")
        return this.user != null && this.user.getRoles() != null ? this.user.getRoleString() : "UNKNOWN";
    }

    // --- getters ו-setters קיימים, לדוגמה: ---

    public Long getCommentId() {
        return commentId;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    // ... (שאר ה-getters וה-setters) ...
}