package com.example.newquiz.repository;

import com.example.newquiz.domain.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Boolean existsByNewsId(Long newsId);
}
