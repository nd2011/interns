package com.example.demo.Repository;

import com.example.demo.Entity.MyAppUser;
import com.example.demo.Entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    // Tìm các dự án mà người dùng đã được phân công
    List<Project> findByAssignedUsers(MyAppUser user);  // Đảm bảo sử dụng đúng tên trường "assignedUsers"
}
