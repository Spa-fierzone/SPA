package com.spazone.service;

import com.spazone.entity.Feedback;

import java.util.List;

public interface FeedbackService {
    void saveFeedback(Feedback feedback);
    List<Feedback> getAllFeedback();
}
