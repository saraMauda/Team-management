package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

public class ReportDTO {
    private Long id;
    private String title;
    private String description;
    private String status;
    private LocalDate reportDate;
    private LocalDate lastEdited;
    private String employeeName;
    private String projectName;
    private Long projectId;
    private Long userId;
    private Integer commentCount;

    public ReportDTO() {
    }

    public ReportDTO(Long id, String title, String description, String status, LocalDate reportDate, LocalDate lastEdited, String employeeName, String projectName, Long projectId, Long userId, Integer commentCount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.reportDate = reportDate;
        this.lastEdited = lastEdited;
        this.employeeName = employeeName;
        this.projectName = projectName;
        this.projectId = projectId;
        this.userId = userId;
        this.commentCount = commentCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public LocalDate getLastEdited() {
        return lastEdited;
    }

    public void setLastEdited(LocalDate lastEdited) {
        this.lastEdited = lastEdited;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }
}
