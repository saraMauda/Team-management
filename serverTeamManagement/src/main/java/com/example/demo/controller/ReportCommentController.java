package com.example.demo.controller;

import com.example.demo.model.Report;
import com.example.demo.model.ReportComment;
import com.example.demo.service.ReportRepository;
import com.example.demo.service.ReportCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/report-comments")
public class ReportCommentController {

    private final ReportRepository reportRepository;
    private final ReportCommentRepository reportCommentRepository;

    @Autowired
    public ReportCommentController(ReportRepository reportRepository,
                                   ReportCommentRepository reportCommentRepository) {
        this.reportRepository = reportRepository;
        this.reportCommentRepository = reportCommentRepository;
    }

    @PostMapping("/add/{reportId}")
    public ResponseEntity<Map<String, Object>> addComment(
            @PathVariable Long reportId,
            @RequestBody Map<String, String> body) {

        String text = body.get("text");
        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        ReportComment comment = new ReportComment();
        comment.setReport(report);
        comment.setText(text);
        comment.setCommentDate(LocalDateTime.now());
        comment.setEdited(false);

        reportCommentRepository.save(comment);

        // ⭐ מחזירים תגובה קטנה בלבד — ללא אובייקטים ענקיים ⭐
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("commentId", comment.getCommentId());
        response.put("text", comment.getText());
        response.put("commentDate", comment.getCommentDate());

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{reportId}")
    public List<Map<String, Object>> getComments(@PathVariable Long reportId) {

        List<ReportComment> list = reportCommentRepository.findByReport_ReportId(reportId);

        // ⭐ מחזירים רק מידע נקי — לא את כל האובייקטים ⭐
        return list.stream().map(c -> {
            Map<String, Object> m = new HashMap<>();
            m.put("commentId", c.getCommentId());
            m.put("text", c.getText());
            m.put("commentDate", c.getCommentDate());
            return m;
        }).toList();
    }
    @GetMapping("/byEmployee/{employeeId}")
    public List<Report> getReportsByEmployee(@PathVariable Long employeeId) {
        return reportRepository.findByReportEmployeeInProject_User_Id(employeeId);
    }

}
