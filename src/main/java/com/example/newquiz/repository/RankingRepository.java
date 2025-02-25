package com.example.newquiz.repository;

import com.example.newquiz.domain.Ranking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RankingRepository extends JpaRepository<Ranking, Long> {
}
