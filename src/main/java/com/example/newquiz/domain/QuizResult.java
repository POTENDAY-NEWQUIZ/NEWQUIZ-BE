package com.example.newquiz.domain;

import com.example.newquiz.common.base.dao.BaseEntity;
import com.example.newquiz.dto.request.QuizRequest;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "quiz_result")
public class QuizResult extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_result_id", nullable = false)
    private Long quizResultId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "quiz_id", nullable = false)
    private Long quizId;

    @Column(name = "user_answer", nullable = false)
    private Integer userAnswer;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;

    @Setter
    @Column(name = "is_checked")
    private Boolean isChecked;

    @Builder
    public static QuizResult toEntity(Long userId, QuizRequest.ResultDto resultDto) {
        return QuizResult.builder()
                .userId(userId)
                .quizId(resultDto.getQuizId())
                .userAnswer(resultDto.getUserAnswer())
                .isCorrect(resultDto.getIsCorrect())
                .build();
    }
}
