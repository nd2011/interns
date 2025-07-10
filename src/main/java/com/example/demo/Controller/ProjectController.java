package com.example.demo.Controller;

import com.example.demo.Entity.MyAppUser;
import com.example.demo.Entity.Project;
import com.example.demo.Repository.MyAppUserRepository;
import com.example.demo.Repository.ProjectRepository;
import com.example.demo.Service.NotificationService;
import com.example.demo.Service.ProjectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/admin/projects")
@PreAuthorize("hasRole('ADMIN')")
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MyAppUserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ProjectService projectService;  // nếu bạn có service xử lý business logic

    // =========== Thymeleaf View ============

    // Hiển thị form tạo project
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("project", new Project());
        model.addAttribute("users", userRepository.findAll());
        return "project/project-create";
    }

    // Xử lý submit form tạo project, nhận thêm participantIds (userIds)
    @PostMapping("/create")
    public String createProject(@ModelAttribute Project project,
                                @RequestParam(value = "participantIds", required = false) Set<Long> participantIds) {
        if (participantIds == null) participantIds = new HashSet<>();

        // Giao cho Service xử lý hết!
        projectService.createProject(project, participantIds);

        // Tạo thông báo cho các user được giao dự án (nên đặt sau khi project đã lưu)
        String title = "Bạn vừa được thêm vào dự án: " + project.getName();
        String link = "/user/projects";
        notificationService.notifyUsers(new ArrayList<>(participantIds), title, link);

        return "redirect:/admin/projects/list";
    }


    // Hiển thị trang phân công user cho project
    @GetMapping("/{projectId}/assign")
    public String showAssignUsers(@PathVariable Long projectId, Model model) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return "redirect:/admin/projects/create";
        }
        Project project = projectOpt.get();
        List<MyAppUser> users = userRepository.findAll();
        model.addAttribute("project", project);
        model.addAttribute("users", users);
        return "project/project-assign";
    }

    // Xử lý phân công user qua form
    @PostMapping("/{projectId}/assign")
    public String assignUsers(@PathVariable Long projectId, @RequestParam(value = "userIds", required = false) List<Long> userIds) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return "redirect:/admin/projects/create";
        }
        Project project = projectOpt.get();
        if (userIds != null && !userIds.isEmpty()) {
            List<MyAppUser> users = userRepository.findAllById(userIds);
            project.getAssignedUsers().addAll(users);
            projectRepository.save(project);
        }
        return "redirect:/admin/projects/list";
    }

    // Danh sách dự án (view)
    @GetMapping("/list")
    public String listProjects(Model model) {
        List<Project> projects = projectRepository.findAll();
        model.addAttribute("projects", projects);
        model.addAttribute("project", new Project());
        model.addAttribute("users", userRepository.findAll());
        return "project/project-list";
    }

    // =========== REST API JSON ============

    // API tạo dự án (JSON)
    @PostMapping("/api/create")
    @ResponseBody
    public ResponseEntity<Project> apiCreateProject(@RequestBody Project project) {
        Project saved = projectRepository.save(project);
        return ResponseEntity.ok(saved);
    }

    // API phân công user cho project
    @PostMapping("/api/{projectId}/assign")
    @ResponseBody
    public ResponseEntity<?> apiAssignUsers(@PathVariable Long projectId, @RequestBody List<Long> userIds) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Project project = projectOpt.get();
        List<MyAppUser> users = userRepository.findAllById(userIds);
        project.getAssignedUsers().addAll(users);
        projectRepository.save(project);
        return ResponseEntity.ok("Users assigned successfully.");
    }
}
