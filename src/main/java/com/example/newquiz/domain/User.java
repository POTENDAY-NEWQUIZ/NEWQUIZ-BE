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

    @Builder
    public static User toEntity(String providerId, String nickName, LocalDate birth) {
        return User.builder()
                .providerId(providerId)
                .nickName(nickName)
                .birth(birth)
                .build();
    }
}
