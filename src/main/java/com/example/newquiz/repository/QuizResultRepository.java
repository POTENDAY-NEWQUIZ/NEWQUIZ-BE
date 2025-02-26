package com.example.newquiz.repository;

import com.example.newquiz.domain.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {
    boolean existsByUserIdAndQuizIdAndIsCorrect(Long userId, Long quizId, Boolean isCorrect);
}
