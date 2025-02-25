package com.example.newquiz.repository;

import com.example.newquiz.domain.News;
import com.example.newquiz.domain.enums.NewsCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {
    @Query("SELECT n FROM News n WHERE n.category = :category ORDER BY n.date DESC LIMIT 20")
    List<News> findByCategory(NewsCategory category);
}

