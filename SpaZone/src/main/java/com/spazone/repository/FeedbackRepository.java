package com.spazone.repository;

import com.spazone.entity.Feedback;
import com.spazone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    List<Feedback> findByUser(User user);
}
