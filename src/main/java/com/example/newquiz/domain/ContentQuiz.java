package com.example.newquiz.domain;

import com.example.newquiz.common.base.dao.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "content_quiz")
public class ContentQuiz extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_quiz_id", nullable = false)
    private Long contentQuizId;

    @Column(name = "quiz_id", nullable = false)
    private Long quizId;

    @Column(name = "answer", nullable = false)
    private Boolean answer;

    @Column(name = "explanation", nullable = false)
    private String explanation;

    @Column(name = "question", nullable = false)
    private String question;
}
