package com.example.newquiz.repository;

import com.example.newquiz.domain.CompletedNews;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CompletedNewsRepository extends JpaRepository<CompletedNews, Long> {
    List<CompletedNews> findByUserId(Long userId);
    boolean existsByUserIdAndNewsId(Long userId, Long newsId);
    Optional<CompletedNews> findByUserIdAndNewsId(Long userId, Long newsId);
    void deleteByUserId(Long userId);
    List<CompletedNews> findByUserIdAndIsCompletedTrueOrderByUpdatedAtDesc(Long userId);

    List<CompletedNews> findAllByNewsId(Long newsId);
    @Query("SELECT COUNT(c) FROM CompletedNews c WHERE c.userId = :userId AND c.isCompleted = true")
    int countByUserIdAndIsCompletedTrue(Long userId);

    @Query("SELECT DATE(c.updatedAt), COUNT(c) FROM CompletedNews c " +
            "WHERE c.userId = :userId AND c.isCompleted = true " +
            "AND DATE(c.updatedAt) >= :oneWeekAgo " +
            "GROUP BY DATE(c.updatedAt)")
    List<Object[]> countByUserIdAndIsCompletedTrueGroupByDate(
            @Param("userId") Long userId,
            @Param("oneWeekAgo") LocalDate oneWeekAgo);

}
