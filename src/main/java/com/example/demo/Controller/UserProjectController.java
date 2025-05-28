package com.example.demo.Controller;

import com.example.demo.Entity.MyAppUser;
import com.example.demo.Entity.Project;
import com.example.demo.Repository.MyAppUserRepository;
import com.example.demo.Repository.ProjectRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/user/projects")
@PreAuthorize("hasAnyRole('USER','ADMIN')")
public class UserProjectController {

    @Autowired
    private MyAppUserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @GetMapping
    public String viewMyProjects(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<MyAppUser> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }
        MyAppUser user = userOpt.get();

        Set<Project> projects = user.getProjects();
        model.addAttribute("projects", projects);

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);

        if (isAdmin) {
            List<Project> allProjects = projectRepository.findAll();
            model.addAttribute("allProjects", allProjects);
        }

        return "project/user-project-list";
    }
}
