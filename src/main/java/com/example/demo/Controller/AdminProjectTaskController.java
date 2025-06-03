package com.example.demo.Controller;

import com.example.demo.Entity.Project;
import com.example.demo.Entity.Task;
import com.example.demo.Service.TaskService;
import com.example.demo.Repository.ProjectRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminProjectTaskController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskService taskService;

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

    @PostMapping("/projects/{projectId}/tasks/create")
    public String createTask(@PathVariable Long projectId,
                             @RequestParam String name,
                             @RequestParam String description,
                             @RequestParam Long assignedUserId,
                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deadline,
                             RedirectAttributes redirectAttrs) {
        try {
            taskService.createTask(projectId, name, description, assignedUserId, deadline);
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
}
