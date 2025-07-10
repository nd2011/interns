package com.example.demo.Controller;

import com.example.demo.Entity.MyAppUser;
import com.example.demo.Service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/notifications/mark-all-read")
    public void markAllRead(@AuthenticationPrincipal MyAppUser currentUser) {
        if (currentUser != null) {
            notificationService.markAllAsRead(currentUser.getId());
        }
    }
}
