package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Getter
@Setter
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ראש צוות
    @ManyToOne
    @JoinColumn(name = "leader_id")
    private Users leader;

    // חברי צוות – לא נטען אוטומטית
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<TeamMember> members;
}
