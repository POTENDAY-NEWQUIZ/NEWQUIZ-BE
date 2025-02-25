package com.example.newquiz.repository;

import com.example.newquiz.domain.Paragraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParagraphRepository extends JpaRepository<Paragraph, Long> {
    List<Paragraph> findByNewsIdOrderByContentOrderAsc(Long newsId);
}
