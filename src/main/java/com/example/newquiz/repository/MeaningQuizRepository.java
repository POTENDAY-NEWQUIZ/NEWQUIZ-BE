package com.example.newquiz.repository;

import com.example.newquiz.domain.MeaningQuiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeaningQuizRepository extends JpaRepository<MeaningQuiz, Long> {
}
