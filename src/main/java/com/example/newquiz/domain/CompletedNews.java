package com.example.newquiz.domain;

import com.example.newquiz.common.base.dao.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "completed_news")
public class CompletedNews extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "completed_news_id", nullable = false)
    private Long completedNewsId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "news_id", nullable = false)
    private Long newsId;

    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted;

    @Builder
    public static CompletedNews toEntity(Long userId, Long newsId, Boolean isCompleted) {
        return CompletedNews.builder()
                .userId(userId)
                .newsId(newsId)
                .isCompleted(isCompleted)
                .build();
    }
}
