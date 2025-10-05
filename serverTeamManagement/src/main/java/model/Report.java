package model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long reportId;

    private String reportTitle;
    private String reportDescription;
    private LocalDate reportDate;
    @ManyToOne
    @JoinColumn(name = "employeeProjectId")
    private EmployeeInProject reportEmployeeInProject;
    @OneToMany(mappedBy = "report")
    private List<ReportComment> reportComments;

    public Report() {
    }

    public Report(long reportId, String reportTitle, String reportDescription, LocalDate reportDate, EmployeeInProject reportEmployeeInProject, List<ReportComment> reportComments) {
        this.reportId = reportId;
        this.reportTitle = reportTitle;
        this.reportDescription = reportDescription;
        this.reportDate = reportDate;
        this.reportEmployeeInProject = reportEmployeeInProject;
        this.reportComments = reportComments;
    }

    public long getReportId() {
        return reportId;
    }

    public void setReportId(long reportId) {
        this.reportId = reportId;
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public String getReportDescription() {
        return reportDescription;
    }

    public void setReportDescription(String reportDescription) {
        this.reportDescription = reportDescription;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public EmployeeInProject getReportEmployeeInProject() {
        return reportEmployeeInProject;
    }

    public void setReportEmployeeInProject(EmployeeInProject reportEmployeeInProject) {
        this.reportEmployeeInProject = reportEmployeeInProject;
    }

    public List<ReportComment> getReportComments() {
        return reportComments;
    }

    public void setReportComments(List<ReportComment> reportComments) {
        this.reportComments = reportComments;
    }
}
