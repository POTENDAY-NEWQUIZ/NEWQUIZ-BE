package com.example.newquiz.domain;

import com.example.newquiz.common.base.dao.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ranking")
public class Ranking extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ranking_id", nullable = false)
    private Long rankingId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "score", nullable = false)
    private Integer score;
}
