package com.example.demo.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_user_id")
    private MyAppUser assignedUser;

    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    public enum Status {
        PENDING,
        DONE
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    // Thêm phương thức getter cho tên của project
    public String getProjectName() {
        if (project != null) {
            return project.getName();  // Giả sử Project có phương thức getName()
        }
        return "Chưa có dự án";
    }

    public MyAppUser getAssignedUser() { return assignedUser; }
    public void setAssignedUser(MyAppUser assignedUser) { this.assignedUser = assignedUser; }
    public LocalDate getDeadline() { return deadline; }
    public String getDeadlineFormatted() {
        if (deadline == null) return "Chưa đặt hạn";
        return deadline.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
