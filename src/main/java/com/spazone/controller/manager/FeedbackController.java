package com.spazone.controller.manager;

import com.spazone.entity.Feedback;
import com.spazone.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/manager/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    // Hiển thị tất cả feedback với phân trang
    @GetMapping
    public String listFeedback(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Feedback> feedbackPage = feedbackRepository.findAllByOrderByCreatedAtDesc(pageable);

        // Thêm dữ liệu vào model
        model.addAttribute("feedbackPage", feedbackPage);
        model.addAttribute("feedbacks", feedbackPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", feedbackPage.getTotalPages());
        model.addAttribute("totalElements", feedbackPage.getTotalElements());

        // Thêm helper method vào model để sử dụng trong template
        model.addAttribute("starRatingHelper", this);

        return "manager/feedback-list";
    }

    // Method public để có thể sử dụng trong template
    public String getStarRating(Integer rating) {
        if (rating == null) return "";
        StringBuilder stars = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            if (i <= rating) {
                stars.append("★");
            } else {
                stars.append("☆");
            }
        }
        return stars.toString();
    }

    // Thêm method để tính toán thống kê (nếu cần)
    public double calculateAverageRating(Page<Feedback> feedbackPage) {
        return feedbackPage.getContent().stream()
                .mapToDouble(Feedback::getAverageRating)
                .average()
                .orElse(0.0);
    }
}