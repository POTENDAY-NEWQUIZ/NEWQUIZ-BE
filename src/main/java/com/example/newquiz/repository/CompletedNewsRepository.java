package com.example.newquiz.repository;

import com.example.newquiz.domain.CompletedNews;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompletedNewsRepository extends JpaRepository<CompletedNews, Long> {
    List<CompletedNews> findByUserId(Long userId);
    boolean existsByUserIdAndNewsId(Long userId, Long newsId);
}
