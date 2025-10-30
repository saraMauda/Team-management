package com.example.demo.service;

import com.example.demo.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByProjectLeader_Id(Long leaderId);

    List<Project> findByProjectEmployeeProjects_User_Id(Long userId);
}
