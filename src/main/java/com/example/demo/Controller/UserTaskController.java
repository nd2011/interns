package com.example.demo.Controller;

import com.example.demo.Entity.MyAppUser;
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

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserTaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private MyAppUserRepository userRepository;

    // Hiển thị danh sách task user được giao
    @GetMapping("/tasks")
    public String listUserTasks(Model model, Principal principal) {
        // Lấy username hiện tại
        String username = principal.getName();

        MyAppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        // Lấy danh sách task của user
        List<Task> tasks = taskService.getTasksByUser(user);

        model.addAttribute("tasks", tasks);
        return "project/user-tasks";
    }

    // User báo cáo hoàn thành task
    @PostMapping("/tasks/{taskId}/report-complete")
    public String reportCompleteTask(@PathVariable Long taskId, Principal principal, RedirectAttributes redirectAttrs) {
        String username = principal.getName();
        MyAppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        try {
            taskService.reportComplete(taskId, user);
            redirectAttrs.addFlashAttribute("success", "Đã báo cáo hoàn thành nhiệm vụ.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/user/tasks";
    }
}
