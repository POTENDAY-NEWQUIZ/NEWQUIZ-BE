package com.example.newquiz.domain.enums;

import com.example.newquiz.common.exception.GeneralException;
import com.example.newquiz.common.status.ErrorStatus;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum NewsCategory {
    POLITICS("정치"), ECONOMY("경제"), SOCIETY("사회"), ETC("기타");

    private final String value;

    public String getValue() {
        return value;
    }

    public static NewsCategory getNewsCategory(String value) {
        for (NewsCategory newsCategory : values()) {
            if (newsCategory.getValue().equals(value)) {
                return newsCategory;
            }
        }
        throw new GeneralException(ErrorStatus.BAD_REQUEST);
    }
}
