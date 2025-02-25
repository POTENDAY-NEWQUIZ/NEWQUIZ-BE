package com.example.newquiz.repository;

import com.example.newquiz.domain.CompletedNews;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompletedNewsRepository extends JpaRepository<CompletedNews, Long> {
}
