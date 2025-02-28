package com.example.newquiz.dto.response;

import com.example.newquiz.domain.enums.NewsCategory;
import com.example.newquiz.domain.enums.QuizType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteDto {
    private Long quizResultId;
    private NewsCategory category;
    private String title;
    private LocalDate date;
    private QuizType type;
    private Boolean isChecked;
}
