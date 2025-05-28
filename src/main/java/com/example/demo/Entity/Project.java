package com.example.demo.Entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String description;

    // Mối quan hệ nhiều nhiều giữa Project và User (MyAppUser)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "project_users",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<MyAppUser> assignedUsers = new HashSet<>();

    // Getter, Setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<MyAppUser> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(Set<MyAppUser> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }
}
