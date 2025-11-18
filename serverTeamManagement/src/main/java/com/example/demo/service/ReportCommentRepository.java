package com.example.demo.service;

import com.example.demo.model.ReportComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportCommentRepository extends JpaRepository<ReportComment, Long> {

    // ⭐ תיקון קריטי: שימוש בשאילתת @Query מפורשת עם פרמטר :reportId ⭐
    // זה מונע מ-Spring לנסות לפרש את השם באופן אוטומטי
    @Query("SELECT c FROM ReportComment c JOIN FETCH c.user u WHERE c.report.reportId = :reportId")
    List<ReportComment> findByReportIdWithUser(@Param("reportId") Long reportId); // ⭐ הוספת @Param ⭐

    // במידה ואתה עדיין רוצה להשאיר את findByReport_ReportId (אם כי פחות יעיל):
    List<ReportComment> findByReport_ReportId(Long reportId);
}
