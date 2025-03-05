package com.example.newquiz.domain;

import com.example.newquiz.common.base.dao.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "paragraph")
public class Paragraph extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paragraph_id", nullable = false)
    private Long paragraphId;

    @Column(name = "news_id", nullable = false)
    private Long newsId;

    @Column(name = "content_order", nullable = false)
    private Integer contentOrder;

    @Lob
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Lob
    @Setter
    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Builder
    public static Paragraph toEntity(Long newsId, Integer content_order, String content) {
        return Paragraph.builder()
                .newsId(newsId)
                .contentOrder(content_order)
                .content(content)
                .build();
    }
}
