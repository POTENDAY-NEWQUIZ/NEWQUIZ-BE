package com.example.newquiz.domain;

import com.example.newquiz.common.base.dao.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "synonym_quiz")
public class SynonymQuiz extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "synonym_quiz_id", nullable = false)
    private Long synonymQuizId;

    @Column(name = "quiz_id", nullable = false)
    private Long quizId;

    @Column(name = "answer", nullable = false)
    private Integer answer;

    @Column(name = "option1", nullable = false)
    private String option1;

    @Column(name = "option2", nullable = false)
    private String option2;

    @Column(name = "option3", nullable = false)
    private String option3;

    @Column(name = "option4", nullable = false)
    private String option4;

    @Column(name = "explanation")
    private String explanation;

    @Column(name = "source_sentence", nullable = false)
    private String sourceSentence;

    @Column(name = "example")
    private String example;
}
