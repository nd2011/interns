package com.example.demo.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContentController {

    // Trang login
    @GetMapping("/req/login")
    public String login(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // Lấy tên người dùng đã đăng nhập
        model.addAttribute("username", username); // Truyền vào model
        return "login"; // Trang login
    }

    // Trang signup
    @GetMapping("/req/signup")
    public String signup(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // Lấy tên người dùng đã đăng nhập
        model.addAttribute("username", username); // Truyền vào model
        return "signup"; // Trang signup
    }

    // Trang index (home)
    @GetMapping("/index")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // Lấy tên người dùng đã đăng nhập
        model.addAttribute("username", username); // Truyền vào model
        return "index"; // Trang index
    }
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied"; // trả về file access-denied.html
    }
}
