package com.example.demo.Repository;

import com.example.demo.Entity.MyAppUser;
import com.example.demo.Entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    // Tìm các nhiệm vụ theo projectId
    List<Task> findByProjectId(Long projectId);

    // Tìm các nhiệm vụ theo assignedUser
    List<Task> findByAssignedUser(MyAppUser assignedUser);

    // Phương thức đúng tìm theo assignedUser và projectId
    List<Task> findByAssignedUser_IdAndProject_Id(Long userId, Long projectId);
}
