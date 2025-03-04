package com.example.newquiz.domain;

import com.example.newquiz.common.base.dao.BaseEntity;
import com.example.newquiz.domain.enums.QuizType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "quiz")
public class Quiz extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_id", nullable = false)
    private Long quizId;

    @Column(name = "news_id", nullable = false)
    private Long newsId;

    @Column(name = "paragraph_id", nullable = false)
    private Long paragraphId;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private QuizType type;

    @Setter
    @Column(name = "sysnonym_quiz_id")
    private Long synonymQuizId;

    @Setter
    @Column(name = "antonym_quiz_id")
    private Long antonymQuizId;

    @Setter
    @Column(name = "meaning_quiz_id")
    private Long meaningQuizId;

    @Setter
    @Column(name = "content_quiz_id")
    private Long contentQuizId;
}
