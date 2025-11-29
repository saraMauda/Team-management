package com.example.demo.service;

import com.example.demo.model.ReportComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportCommentRepository extends JpaRepository<ReportComment, Long> {

    @Query("SELECT c FROM ReportComment c JOIN FETCH c.user u WHERE c.report.reportId = :reportId")
    List<ReportComment> findByReportIdWithUser(@Param("reportId") Long reportId);

    List<ReportComment> findByReport_ReportId(Long reportId);
}
