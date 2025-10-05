package model;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;
    @OneToMany(mappedBy="user")
    private List<EmployeeInProject> employeeProjects;
    @OneToMany(mappedBy="projectLeader")
    private List<Project> leaderProjects;

}
