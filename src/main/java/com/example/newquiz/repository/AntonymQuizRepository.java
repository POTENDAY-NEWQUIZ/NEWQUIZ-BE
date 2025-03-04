package com.example.newquiz.repository;

import com.example.newquiz.domain.AntonymQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AntonymQuizRepository extends JpaRepository<AntonymQuiz, Long> {
    @Query("SELECT s FROM AntonymQuiz s WHERE s.quizId IN :quizId")
    List<AntonymQuiz> findAllByQuizId(List<Long> quizId);
}
