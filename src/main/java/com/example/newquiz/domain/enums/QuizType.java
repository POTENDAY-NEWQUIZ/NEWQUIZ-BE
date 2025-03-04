package com.example.newquiz.domain.enums;

import com.example.newquiz.common.status.ErrorStatus;
import com.example.newquiz.common.exception.GeneralException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum QuizType {
    SYNONYM("유의어"), MEANING("단어뜻"), CONTENT("내용일치"), ANTONYM("반의어");

    private final String value;

    public String getValue() {
        return value;
    }

    public static QuizType getQuizType(String value) {
        for (QuizType quizType : values()) {
            if (quizType.getValue().equals(value)) {
                return quizType;
            }
        }
        throw new GeneralException(ErrorStatus.BAD_REQUEST);
    }


}
