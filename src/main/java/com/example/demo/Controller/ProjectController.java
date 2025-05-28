package com.example.demo.Controller;

import com.example.demo.Entity.MyAppUser;
import com.example.demo.Entity.Project;
import com.example.demo.Repository.MyAppUserRepository;
import com.example.demo.Repository.ProjectRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller  // Dùng cho Thymeleaf + API, chú ý trả ResponseEntity cho API
@RequestMapping("/admin/projects")
@PreAuthorize("hasRole('ADMIN')")  // Chỉ admin truy cập toàn bộ controller
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MyAppUserRepository userRepository;

    // =========== Thymeleaf View ============

    // Hiển thị form tạo project
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("project", new Project());
        return "project/project-create";
    }

    // Xử lý submit form tạo project
    @PostMapping("/create")
    public String createProject(@ModelAttribute Project project) {
        Project savedProject = projectRepository.save(project);
        return "redirect:/admin/projects/" + savedProject.getId() + "/assign";
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
