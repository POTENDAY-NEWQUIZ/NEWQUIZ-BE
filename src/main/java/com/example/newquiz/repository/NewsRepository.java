package com.example.newquiz.repository;

import com.example.newquiz.domain.News;
import com.example.newquiz.domain.enums.NewsCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {
    @Query("SELECT n FROM News n " +
            "WHERE n.level = :level " +
            "AND n.category = :category " +
            "AND NOT EXISTS (SELECT 1 FROM CompletedNews cn WHERE cn.newsId = n.newsId AND cn.userId = :userId) " +
            "ORDER BY n.date DESC")
    List<News> findByLevelAndCategoryOrderByDateDesc(
            Long userId, String level, NewsCategory category, Pageable pageable);

    @Query("SELECT n FROM News n " +
            "WHERE n.category = :category " +
            "AND NOT EXISTS (SELECT 1 FROM CompletedNews cn WHERE cn.newsId = n.newsId AND cn.userId = :userId) " +
            "ORDER BY n.date DESC")
    List<News> findByCategoryOrderByDateDesc(Long userId, NewsCategory category, Pageable pageable);

}