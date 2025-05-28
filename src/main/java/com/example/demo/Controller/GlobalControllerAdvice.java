package com.example.demo.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    // Phương thức này sẽ được gọi cho tất cả các controller
    @ModelAttribute("username")
    public String username() {
        // Lấy thông tin người dùng hiện tại từ SecurityContext
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return auth.getName();  // Trả về username
        }
        return null;  // Nếu chưa đăng nhập, trả về null
    }
}
