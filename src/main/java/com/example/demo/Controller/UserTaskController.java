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

import java.security.Principal;
import java.util.List;
import java.util.Set;

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
        String username = principal.getName();

        MyAppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        // Đảm bảo rằng các dự án đã được tải (nếu bạn sử dụng FetchType.LAZY, hãy đảm bảo chúng được tải tại đây)
        Set<Project> projects = user.getProjects();

        // Lấy danh sách nhiệm vụ của user từ service
        List<Task> tasks = taskService.getTasksByUser(user);

        model.addAttribute("user", user);  // Đảm bảo user có trong model
        model.addAttribute("tasks", tasks);  // Đảm bảo tasks có trong model

        return "project/user-tasks";  // Trả về view user-tasks.html
    }

    // User báo cáo hoàn thành task
    @PostMapping("/tasks/{taskId}/report-complete")
    public String reportCompleteTask(@PathVariable Long taskId, Principal principal, RedirectAttributes redirectAttrs) {
        // Lấy username hiện tại
        String username = principal.getName();

        // Tìm kiếm user từ username
        MyAppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        try {
            // Gọi service để báo cáo hoàn thành nhiệm vụ
            taskService.reportComplete(taskId, user);
            redirectAttrs.addFlashAttribute("success", "Đã báo cáo hoàn thành nhiệm vụ.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/user/tasks";  // Quay lại danh sách nhiệm vụ của user
    }
}
