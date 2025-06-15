package com.spazone.repository;

import com.spazone.entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {

    // Tìm tất cả feedback theo thứ tự thời gian mới nhất
    List<Feedback> findAllByOrderByCreatedAtDesc();

    // Tìm tất cả feedback với phân trang theo thứ tự thời gian mới nhất
    Page<Feedback> findAllByOrderByCreatedAtDesc(Pageable pageable);
}