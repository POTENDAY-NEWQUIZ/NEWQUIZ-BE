package com.example.newquiz.repository;

import com.example.newquiz.domain.Ranking;
import com.example.newquiz.dto.response.RankingUserDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RankingRepository extends JpaRepository<Ranking, Long> {
    Ranking findByUserId(Long userId);
    void deleteByUserId(Long userId);


    @Query("SELECT new com.example.newquiz.dto.response.RankingUserDto(" +
            "u.userId, " +
            "(SELECT COUNT(DISTINCT r2.score) + 1 FROM Ranking r2 WHERE r2.score > r.score), " +
            "u.nickName, " +
            "r.score, " +
            "u.profileImageUrl) " +
            "FROM Ranking r, User u " +
            "WHERE r.userId = u.userId " +
            "ORDER BY r.score DESC")
    List<RankingUserDto> findAllRankingsWithUserDetails();

}
