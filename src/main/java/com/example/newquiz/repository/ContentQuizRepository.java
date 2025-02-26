package com.example.newquiz.repository;

import com.example.newquiz.domain.ContentQuiz;
import com.example.newquiz.domain.MeaningQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContentQuizRepository extends JpaRepository<ContentQuiz, Long> {
    @Query("SELECT s FROM ContentQuiz s WHERE s.quizId IN :quizId")
    List<ContentQuiz> findAllByQuizId(List<Long> quizId);
}
