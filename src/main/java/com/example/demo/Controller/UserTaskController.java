package com.example.demo.Controller;

import com.example.demo.Entity.MyAppUser;
import com.example.demo.Entity.Project;
import com.example.demo.Entity.Task;
import com.example.demo.Repository.MyAppUserRepository;
import com.example.demo.Service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/user")
@Tag(name = "Nhiệm vụ người dùng", description = "Xem và báo cáo các task được giao")
public class UserTaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private MyAppUserRepository userRepository;

    @Operation(summary = "Lấy danh sách task mà người dùng đang đảm nhiệm")
    @GetMapping("/tasks")
    public String listUserTasks(Model model, Principal principal) {
        String username = principal.getName();
        MyAppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        List<Project> projects = user.getProjects();
        List<Task> tasks = taskService.getTasksByUser(user);

        model.addAttribute("user", user);
        model.addAttribute("tasks", tasks);

        return "project/user-tasks";
    }

    @Operation(summary = "Người dùng báo cáo hoàn thành task theo ID")
    @PostMapping("/tasks/{taskId}/report-complete")
    public String reportCompleteTask(@PathVariable Long taskId, Principal principal, RedirectAttributes redirectAttrs) {
        String username = principal.getName();
        MyAppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        try {
            taskService.reportComplete(taskId, user);
            redirectAttrs.addFlashAttribute("success", "Đã báo cáo hoàn thành nhiệm vụ.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/user/tasks";
    }
}

