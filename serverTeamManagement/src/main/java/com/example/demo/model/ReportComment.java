package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class ReportComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;
    @ManyToOne
    @JoinColumn(name = "reportId")
    private Report report;
    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonIgnore
    private Users user;
    private String text;
    private LocalDateTime commentDate;
    private boolean isEdited;

    public ReportComment() {
    }

    public Long getUserId() {
        return this.user != null ? this.user.getId() : null;
    }

    public String getAuthorRole() {
        return this.user != null && this.user.getRoles() != null ? this.user.getRoleString() : "UNKNOWN";
    }

}