package com.spazone.controller;

import com.spazone.entity.Feedback;
import com.spazone.entity.User;
import com.spazone.service.FeedbackService;
import com.spazone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private UserService userService;

    // Hiển thị form tạo phản hồi
    @GetMapping("/create")
    public String showFeedbackForm(Model model) {
        model.addAttribute("feedback", new Feedback());
        return "feedback/form";
    }

    // Submit phản hồi
    @PostMapping("/submit")
    public String submitFeedback(@ModelAttribute Feedback feedback, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        feedback.setUser(user);
        feedbackService.saveFeedback(feedback);
        return "feedback/success";
    }

    // Xem tất cả phản hồi (dành cho Admin)
    @GetMapping("/all")
    public String viewAllFeedback(Model model) {
        List<Feedback> feedbackList = feedbackService.getAllFeedback();
        model.addAttribute("feedbacks", feedbackList);
        return "feedback/list";
    }
}
