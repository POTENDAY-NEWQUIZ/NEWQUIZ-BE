package com.example.newquiz.repository;

import com.example.newquiz.domain.ContentQuiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentQuizRepository extends JpaRepository<ContentQuiz, Long> {
}
