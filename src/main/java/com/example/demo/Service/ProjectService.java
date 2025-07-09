package com.example.demo.Service;

import com.example.demo.Entity.Project;
import com.example.demo.Entity.MyAppUser;
import com.example.demo.Repository.ProjectRepository;
import com.example.demo.Repository.MyAppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.List;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MyAppUserRepository userRepository;

    // Hàm tạo project và gán user (bạn có thể điều chỉnh logic này tùy nhu cầu)
    public Project createProject(Project project, Set<Long> participantIds) {
        if (participantIds != null && !participantIds.isEmpty()) {
            List<MyAppUser> users = userRepository.findAllById(participantIds);
            project.setAssignedUsers((Set<MyAppUser>) users); // Chú ý đúng tên field assignedUsers của entity Project
        }
        return projectRepository.save(project);
    }
}
