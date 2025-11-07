package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Enumerated(EnumType.STRING)
    private ERole name;
    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    private List<Users> usersList;

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public ERole getName() {
        return name;
    }

    public void setName(ERole name) {
        this.name = name;
    }

    public List<Users> getUsersList() {
        return usersList;
    }

    public void setUsersList(List<Users> usersList) {
        this.usersList = usersList;
    }
}

