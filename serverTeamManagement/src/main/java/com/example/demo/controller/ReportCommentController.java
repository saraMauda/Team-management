package com.example.demo.controller;

import com.example.demo.model.Report;
import com.example.demo.model.ReportComment;
import com.example.demo.model.Users; // נדרש כדי לטעון משתמש
import com.example.demo.service.ReportRepository;
import com.example.demo.service.ReportCommentRepository;
import com.example.demo.service.UsersRepository; // ⭐ שינוי: שימוש ב-UsersRepository במקום UserService ⭐

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/report-comments")
public class ReportCommentController {

    private final ReportRepository reportRepository;
    private final ReportCommentRepository reportCommentRepository;
    private final UsersRepository usersRepository; // ⭐ הזרקת UsersRepository ⭐

    @Autowired
    public ReportCommentController(ReportRepository reportRepository,
                                   ReportCommentRepository reportCommentRepository,
                                   UsersRepository usersRepository) { // ⭐ הוספת UsersRepository לבנאי
        this.reportRepository = reportRepository;
        this.reportCommentRepository = reportCommentRepository;
        this.usersRepository = usersRepository;
    }

    @PostMapping("/add/{reportId}")
    public ResponseEntity<ReportComment> addComment(
            @PathVariable Long reportId,
            @RequestBody Map<String, String> body) {

        String text = body.get("text");
        String userIdStr = body.get("userId"); // מזהה המשתמש שכותב, נשלח מה-Front-end/body

        if (text == null || text.trim().isEmpty() || userIdStr == null) {
            return ResponseEntity.badRequest().build();
        }

        Long userId = Long.parseLong(userIdStr);

        // ⭐ טוען את אובייקט המשתמש באמצעות UsersRepository ⭐
        Users currentUser = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found: " + reportId));

        ReportComment comment = new ReportComment();
        comment.setReport(report);
        comment.setText(text);
        comment.setCommentDate(LocalDateTime.now());
        comment.setEdited(false);
        comment.setUser(currentUser); // הקישור עכשיו תקין

        reportCommentRepository.save(comment);

        // Spring יבצע סריאליזציה של האובייקט המלא, כולל ה-getters החדשים
        return ResponseEntity.ok(comment);
    }

    @GetMapping("/{reportId}")
    public List<ReportComment> getComments(@PathVariable Long reportId) {

        // ⭐ שימוש במתודה שמוודאת טעינת משתמשים (JOIN FETCH) ⭐
        List<ReportComment> list = reportCommentRepository.findByReportIdWithUser(reportId);
        return list;
    }

    @GetMapping("/byEmployee/{employeeId}")
    public List<Report> getReportsByEmployee(@PathVariable Long employeeId) {
        return reportRepository.findByReportEmployeeInProject_User_Id(employeeId);
    }
}