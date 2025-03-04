package com.example.newquiz.repository;

import com.example.newquiz.domain.Ranking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RankingRepository extends JpaRepository<Ranking, Long> {
    Ranking findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
