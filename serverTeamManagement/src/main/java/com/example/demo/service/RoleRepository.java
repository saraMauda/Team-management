package com.example.demo.service;

import com.example.demo.model.Role;
import com.example.demo.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long> {
}

