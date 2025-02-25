package com.example.newquiz.repository;

import com.example.newquiz.domain.Paragraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ParagraphRepository extends JpaRepository<Paragraph, Long> {
    List<Paragraph> findByNewsIdOrderByContentOrderAsc(Long newsId);
    void deleteByNewsId(Long newsId);

    @Query("SELECT p FROM Paragraph p WHERE p.newsId = :newsId ORDER BY p.contentOrder ASC")
    List<Paragraph> findByNewsId(Long newsId);
}
