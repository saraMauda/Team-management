package com.example.demo.service;

import com.example.demo.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByReportEmployeeInProject_User_Id(Long userId);

    List<Report> findByReportEmployeeInProject_User_Email(String email);

    List<Report> findByReportEmployeeInProject_Project_ProjectId(Long projectId);
    List<Report> findByReportEmployeeInProject_EmployeeProjectId(Long employeeProjectId);
    List<Report> findByReportEmployeeInProject_Project_ProjectLeader_Id(Long projectLeaderId);

}
