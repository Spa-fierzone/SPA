package com.spazone.service;

import com.spazone.entity.User;

import javax.management.Notification;
import java.util.List;

public interface NotificationService {
    void notify(User recipient, String title, String content, String type);
    List<Notification> getUnreadByUser(User user);
}

