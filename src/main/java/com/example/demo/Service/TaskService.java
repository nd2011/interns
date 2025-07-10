package com.example.demo.Service;

import com.example.demo.Entity.MyAppUser;
import com.example.demo.Entity.Project;
import com.example.demo.Entity.Task;
import com.example.demo.Repository.TaskRepository;
import com.example.demo.Repository.ProjectRepository;
import com.example.demo.Repository.MyAppUserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MyAppUserRepository userRepository;

    public List<Task> getTasksByProject(Long projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    public void createTask(Long projectId, String name, String description, Long assignedUserId, LocalDate deadline) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project không tồn tại"));
        MyAppUser user = userRepository.findById(assignedUserId).orElseThrow(() -> new RuntimeException("User không tồn tại"));

        if (!project.getAssignedUsers().contains(user)) {
            throw new RuntimeException("User chưa được phân công vào dự án này");
        }

        Task task = new Task();
        task.setProject(project);
        task.setName(name);
        task.setDescription(description);
        task.setAssignedUser(user);
        task.setDeadline(deadline);
        task.setStatus(Task.Status.PENDING);

        taskRepository.save(task);
    }

    public void toggleStatus(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task không tồn tại"));
        // Chỉ toggle giữa DONE và INPROGRESS, hoặc tuỳ bạn muốn
        if (task.getStatus() == Task.Status.DONE) {
            task.setStatus(Task.Status.INPROGRESS);
        } else {
            task.setStatus(Task.Status.DONE);
        }
        taskRepository.save(task);
    }
    public void reportComplete(Long taskId, MyAppUser user) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task không tồn tại"));

        if (!task.getAssignedUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bạn không có quyền báo cáo nhiệm vụ này");
        }
        task.setStatus(Task.Status.DONE);
        taskRepository.save(task);
    }

    public Long getProjectIdByTask(Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task không tồn tại"));
        return task.getProject().getId();
    }
    public List<Task> getTasksByUser(MyAppUser user) {
        return taskRepository.findByAssignedUser(user);
    }
    public void rejectTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task không tồn tại"));
        task.setStatus(Task.Status.REJECTED);
        taskRepository.save(task);
    }

    public Long findProjectIdByTaskId(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task không tồn tại"));
        return task.getProject().getId();
    }
}
