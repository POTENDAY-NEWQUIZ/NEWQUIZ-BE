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
    private Integer content_order;

    @Lob
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;
}
