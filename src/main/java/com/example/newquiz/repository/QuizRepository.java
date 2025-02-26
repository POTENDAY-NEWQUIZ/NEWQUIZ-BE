package com.example.newquiz.repository;

import com.example.newquiz.domain.Quiz;
import com.example.newquiz.domain.enums.QuizType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Boolean existsByNewsId(Long newsId);

    @Query("SELECT q FROM Quiz q WHERE q.newsId = :newsId AND q.type = :type")
    List<Quiz> findByNewsIdAndType(Long newsId, QuizType type);

    @Query("SELECT q.newsId FROM Quiz q WHERE q.quizId = :quizId")
    Long findNewsIdByQuizId(Long quizId);

    List<Quiz> findByNewsId(Long newsId);
}
