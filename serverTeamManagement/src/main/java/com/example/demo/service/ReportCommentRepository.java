package com.example.demo.service;

import com.example.demo.model.ReportComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportCommentRepository extends JpaRepository<ReportComment, Long> {

    List<ReportComment> findByReport_ReportId(Long reportId);
}
