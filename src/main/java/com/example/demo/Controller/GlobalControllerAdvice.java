package com.example.demo.Controller;

import com.example.demo.Entity.MyAppUser;
import com.example.demo.Entity.Notification;
import com.example.demo.Service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.List;

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
    @Autowired
    private NotificationService notificationService;

    @ModelAttribute("notifications")
    public List<Notification> addNotifications(@AuthenticationPrincipal MyAppUser currentUser) {
        if (currentUser != null) {
            return notificationService.getUnreadNotifications(currentUser.getId());
        }
        return Collections.emptyList();
    }
}
