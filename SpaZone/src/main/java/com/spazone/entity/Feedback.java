package com.spazone.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer feedbackId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String content;
    private Integer rating;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // === GETTERS ===
    public Integer getFeedbackId() { return feedbackId; }
    public User getUser() { return user; }
    public String getContent() { return content; }
    public Integer getRating() { return rating; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // === SETTERS ===
    public void setFeedbackId(Integer feedbackId) { this.feedbackId = feedbackId; }
    public void setUser(User user) { this.user = user; }
    public void setContent(String content) { this.content = content; }
    public void setRating(Integer rating) { this.rating = rating; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
