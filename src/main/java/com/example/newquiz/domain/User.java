package com.example.newquiz.domain;

import com.example.newquiz.common.base.dao.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Setter
    @Column(name = "nick_name", length = 8, nullable = false)
    private String nickName;

    @Setter
    @Column(name = "birth", nullable = false)
    private LocalDate birth;

    @Setter
    @Column(name = "max_learning_days")
    private Integer maxLearningDays;

    @Setter
    @Column(name = "max_avg_score")
    private Double maxAvgScore;

    @Setter
    @Column(name = "avg_score")
    private Double avgScore;

    @Setter
    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Builder
    public static User toEntity(String providerId, String nickName, LocalDate birth) {
        return User.builder()
                .providerId(providerId)
                .nickName(nickName)
                .birth(birth)
                .avgScore(0.0)
                .maxAvgScore(0.0)
                .maxLearningDays(0)
                .profileImageUrl("https://newquiz-s3-bucket.s3.ap-northeast-2.amazonaws.com/profile/newquiz-default-profile.jpg")
                .build();
    }
}
