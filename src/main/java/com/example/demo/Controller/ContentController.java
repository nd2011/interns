package com.example.demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Controller
public class ContentController {

    @GetMapping("/req/login")
    public String login(){
        return "login";
    }

    @GetMapping("/req/signup")
    public String signup(){
        return "signup";
    }
    @GetMapping("/index")
    public String home(Principal principal, Model model){
        if (principal != null){
            model.addAttribute("username", principal.getName());
        }
        return "index";
    }
    @GetMapping("/api/checkLogin")
    @ResponseBody
    public String checkLogin(Principal principal) {
        if (principal != null) {
            return "Đã đăng nhập: " + principal.getName();
        }
        return "Chưa đăng nhập!";
    }
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied"; // trả về file access-denied.html
    }
}