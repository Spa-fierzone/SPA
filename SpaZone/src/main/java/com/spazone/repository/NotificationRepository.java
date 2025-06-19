package com.spazone.repository;

import com.spazone.entity.Notification;
import com.spazone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<javax.management.Notification> findByRecipientAndIsReadFalse(User recipient);
}

