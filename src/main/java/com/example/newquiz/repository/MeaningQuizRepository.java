package com.example.newquiz.repository;

import com.example.newquiz.domain.MeaningQuiz;
import com.example.newquiz.domain.SynonymQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MeaningQuizRepository extends JpaRepository<MeaningQuiz, Long> {
    @Query("SELECT s FROM MeaningQuiz s WHERE s.quizId IN :quizId")
    List<MeaningQuiz> findAllByQuizId(List<Long> quizId);
}
