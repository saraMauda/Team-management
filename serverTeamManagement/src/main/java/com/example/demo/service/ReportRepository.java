package com.example.demo.service;

import com.example.demo.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    // דוחות לפי עובד
    List<Report> findByReportEmployeeInProject_User_Id(Long userId);

    // דוחות לפי פרויקט
    List<Report> findByReportEmployeeInProject_Project_ProjectId(Long projectId);
}
