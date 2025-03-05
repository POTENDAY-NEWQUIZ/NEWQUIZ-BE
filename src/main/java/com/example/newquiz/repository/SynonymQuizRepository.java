package com.example.newquiz.repository;

import com.example.newquiz.domain.SynonymQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SynonymQuizRepository extends JpaRepository<SynonymQuiz, Long> {
    @Query("SELECT s FROM SynonymQuiz s WHERE s.quizId IN :quizId")
    List<SynonymQuiz> findAllByQuizId(List<Long> quizId);


}
