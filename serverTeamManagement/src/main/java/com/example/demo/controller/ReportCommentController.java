package com.example.demo.controller;

import com.example.demo.model.Report;
import com.example.demo.model.ReportComment;
import com.example.demo.model.Users;
import com.example.demo.service.ReportRepository;
import com.example.demo.service.ReportCommentRepository;
import com.example.demo.service.UsersRepository;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/report-comments")
public class ReportCommentController {

    private final ReportRepository reportRepository;
    private final ReportCommentRepository reportCommentRepository;
    private final UsersRepository usersRepository;

    @Autowired
    public ReportCommentController(ReportRepository reportRepository,
                                   ReportCommentRepository reportCommentRepository,
                                   UsersRepository usersRepository) {
        this.reportRepository = reportRepository;
        this.reportCommentRepository = reportCommentRepository;
        this.usersRepository = usersRepository;
    }

    @PostMapping("/add/{reportId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE','TEAMLEADER','ADMIN')")
    public ResponseEntity<ReportComment> addComment(
            @PathVariable Long reportId,
            @RequestBody Map<String, String> body) {

        String text = body.get("text");
        String userIdStr = body.get("userId");

        if (text == null || text.trim().isEmpty() || userIdStr == null) {
            return ResponseEntity.badRequest().build();
        }

        Long userId = Long.parseLong(userIdStr);

        Users currentUser = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found: " + reportId));

        ReportComment comment = new ReportComment();
        comment.setReport(report);
        comment.setText(text);
        comment.setCommentDate(LocalDateTime.now());
        comment.setEdited(false);
        comment.setUser(currentUser);

        reportCommentRepository.save(comment);

        return ResponseEntity.ok(comment);
    }

    @GetMapping("/{reportId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE','TEAMLEADER','ADMIN')")
    public List<ReportComment> getComments(@PathVariable Long reportId) {
        return reportCommentRepository.findByReportIdWithUser(reportId);
    }

    @GetMapping("/byEmployee/{employeeId}")
    @PreAuthorize("hasAnyRole('TEAMLEADER','ADMIN')")
    public List<Report> getReportsByEmployee(@PathVariable Long employeeId) {
        return reportRepository.findByReportEmployeeInProject_User_Id(employeeId);
    }
}
