package com.example.newquiz.repository;

import com.example.newquiz.domain.CompletedNews;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompletedNewsRepository extends JpaRepository<CompletedNews, Long> {
    List<CompletedNews> findByUserId(Long userId);
    boolean existsByUserIdAndNewsId(Long userId, Long newsId);
    CompletedNews findByUserIdAndNewsId(Long userId, Long newsId);
    void deleteByUserId(Long userId);
    List<CompletedNews> findByUserIdAndIsCompletedTrueOrderByUpdatedAtDesc(Long userId);

    List<CompletedNews> findAllByNewsId(Long newsId);

}
