package com.example.demo.Service;

import com.example.demo.Entity.Notification;
import com.example.demo.Repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public void notifyUsers(List<Long> userIds, String title, String link) {
        for(Long userId : userIds) {
            Notification noti = new Notification();
            noti.setUserId(userId);
            noti.setTitle(title);
            noti.setLink(link);
            noti.setRead(false);
            noti.setCreatedAt(LocalDateTime.now());
            notificationRepository.save(noti);
        }
    }

    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndReadFalse(userId);
    }
    public void markAllAsRead(Long userId) {
        List<Notification> list = notificationRepository.findByUserIdAndReadFalse(userId);
        for (Notification noti : list) {
            noti.setRead(true);
        }
        notificationRepository.saveAll(list);
    }

}
