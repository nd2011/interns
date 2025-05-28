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

@Controller
@RequestMapping("/admin/projects")
@PreAuthorize("hasRole('ADMIN')")
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MyAppUserRepository userRepository;

    // Form tạo dự án
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("project", new Project());
        return "project/project-create";
    }

    // Xử lý tạo dự án
    @PostMapping("/create")
    public String createProject(@ModelAttribute Project project) {
        Project savedProject = projectRepository.save(project);
        return "redirect:/admin/projects/" + savedProject.getId() + "/assign";
    }

    // Form phân công user cho dự án
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

    // Xử lý phân công user
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

    // Danh sách dự án
    @GetMapping("/list")
    public String listProjects(Model model) {
        List<Project> projects = projectRepository.findAll();
        model.addAttribute("projects", projects);
        return "project/project-list";
    }

    // API tạo dự án (ví dụ)
    @PostMapping("/api/create")
    @ResponseBody
    public ResponseEntity<Project> apiCreateProject(@RequestBody Project project) {
        Project saved = projectRepository.save(project);
        return ResponseEntity.ok(saved);
    }

    // API phân công user (ví dụ)
    @PostMapping("/api/{projectId}/assign")
    @ResponseBody
    public ResponseEntity<?> apiAssignUsers(@PathVariable Long projectId, @RequestBody List<Long> userIds) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) return ResponseEntity.notFound().build();
        Project project = projectOpt.get();
        List<MyAppUser> users = userRepository.findAllById(userIds);
        project.getAssignedUsers().addAll(users);
        projectRepository.save(project);
        return ResponseEntity.ok("Users assigned successfully.");
    }
}
