package com.example.newquiz.domain;

import com.example.newquiz.common.base.dao.BaseEntity;
import com.example.newquiz.domain.enums.NewsCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "news")
public class News extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_id", nullable = false)
    private Long newsId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "source", nullable = false)
    private String source;

    @Setter
    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private NewsCategory category;

    @Setter
    @Column(name = "level")
    private String level;

    @Builder
    public static News toEntity(String title, LocalDate date, String source) {
        return News.builder()
                .title(title)
                .date(date)
                .source(source)
                .build();
    }
}
