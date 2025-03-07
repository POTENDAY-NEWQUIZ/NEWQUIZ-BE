package com.example.newquiz.repository;

import com.example.newquiz.domain.News;
import com.example.newquiz.domain.enums.NewsCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {
    @Query("SELECT n FROM News n " +
            "LEFT JOIN CompletedNews cn ON n.newsId = cn.newsId AND cn.userId = :userId " +
            "WHERE n.level = :level AND n.category = :category " +
            "AND (cn.isCompleted IS NULL OR cn.isCompleted = false) " +
            "ORDER BY n.date DESC")
    List<News> findByLevelAndCategoryOrderByDateDesc(
            Long userId, String level, NewsCategory category, Pageable pageable);

}