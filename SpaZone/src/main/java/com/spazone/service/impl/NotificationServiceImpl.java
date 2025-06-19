package com.spazone.service.impl;

import com.spazone.entity.Notification;
import com.spazone.entity.User;
import com.spazone.repository.NotificationRepository;
import com.spazone.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public void notify(User recipient, String title, String content, String type) {
        Notification noti = new Notification();
        noti.setRecipient(recipient);
        noti.setTitle(title);
        noti.setContent(content);
        noti.setType(type);
        noti.setCreatedAt(LocalDateTime.now());
        noti.setIsRead(false);
        notificationRepository.save(noti);
    }

    @Override
    public List<javax.management.Notification> getUnreadByUser(User user) {
        return notificationRepository.findByRecipientAndIsReadFalse(user);
    }
}
