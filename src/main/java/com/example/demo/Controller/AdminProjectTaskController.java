package com.example.demo.Controller;

import com.example.demo.Entity.MyAppUser;
import com.example.demo.Entity.Project;
import com.example.demo.Entity.Task;
import com.example.demo.Repository.MyAppUserRepository;
import com.example.demo.Service.TaskService;
import com.example.demo.Repository.ProjectRepository;
import com.example.demo.Service.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminProjectTaskController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private MyAppUserRepository myAppUserRepository;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/projects/{projectId}/tasks")
    public String viewProjectTasks(@PathVariable Long projectId, Model model) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project không tồn tại"));
        List<Task> tasks = taskService.getTasksByProject(projectId);

        long totalTasks = tasks.size();
        long doneTasks = tasks.stream().filter(t -> t.getStatus() == Task.Status.DONE).count();

        model.addAttribute("project", project);
        model.addAttribute("tasks", tasks);
        model.addAttribute("donePercent", totalTasks > 0 ? (doneTasks * 100.0) / totalTasks : 0);
        model.addAttribute("totalTasks", totalTasks);
        model.addAttribute("doneTasks", doneTasks);
        model.addAttribute("assignedUsers", project.getAssignedUsers());

        return "project/project-tasks";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/projects/{projectId}/tasks/create")
    public String createTask(@PathVariable Long projectId,
                             @ModelAttribute Task task,
                             @RequestParam(value = "participantIds", required = false) Set<Long> participantIds,
                             RedirectAttributes redirectAttrs) {
        try {
            if (participantIds == null) participantIds = new HashSet<>();

            taskService.createTask(projectId, task.getName(), task.getDescription(),
                    task.getAssignedUser().getId(), task.getDeadline());

            List<Long> userIds = new ArrayList<>(participantIds);
            String title = "Bạn vừa được giao nhiệm vụ: " + task.getName();
            String link = "/admin/projects/" + projectId + "/tasks";
            notificationService.notifyUsers(userIds, title, link);

            redirectAttrs.addFlashAttribute("success", "Tạo nhiệm vụ thành công");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/projects/" + projectId + "/tasks";
    }

    @PostMapping("/tasks/{taskId}/toggle-status")
    public String toggleTaskStatus(@PathVariable Long taskId) {
        taskService.toggleStatus(taskId);
        Long projectId = taskService.getProjectIdByTask(taskId);
        return "redirect:/admin/projects/" + projectId + "/tasks";
    }

    @GetMapping("/users/{userId}")
    public String viewUserDetails(@PathVariable Long userId, Model model) {
        MyAppUser user = myAppUserRepository.findById(userId).orElseThrow(() -> new RuntimeException("User không tồn tại"));
        List<Project> projects = projectRepository.findByAssignedUsers(user);
        model.addAttribute("user", user);
        model.addAttribute("projects", projects);
        return "project/user-details";
    }
}
