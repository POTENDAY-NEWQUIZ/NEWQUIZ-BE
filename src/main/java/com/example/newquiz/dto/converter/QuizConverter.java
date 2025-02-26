package com.example.newquiz.dto.converter;

import com.example.newquiz.domain.ContentQuiz;
import com.example.newquiz.domain.MeaningQuiz;
import com.example.newquiz.domain.Quiz;
import com.example.newquiz.domain.SynonymQuiz;
import com.example.newquiz.domain.enums.QuizType;
import com.example.newquiz.dto.response.QuizResponse;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QuizConverter {
    public static QuizResponse.QuizListDto toQuizListDto(int synonymQuizCount, int meaningQuizCount, int contentQuizCount, List<QuizResponse.SynonymQuizDto> synonymQuizDtoList, List<QuizResponse.MeaningQuizDto> meaningQuizDtoList, List<QuizResponse.ContentQuizDto> contentQuizDtoList) {
        return QuizResponse.QuizListDto.builder()
                .synonymQuizCount(synonymQuizCount)
                .meaningQuizCount(meaningQuizCount)
                .contentQuizCount(contentQuizCount)
                .synonymQuiz(synonymQuizDtoList)
                .meaningQuiz(meaningQuizDtoList)
                .contentQuiz(contentQuizDtoList)
                .build();
    }

    public static List<QuizResponse.SynonymQuizDto> toSynonymQuizDtoList(List<SynonymQuiz> synonymQuizList, List<Quiz> quizList) {
        // Quiz 리스트를 Map으로 변환 (quizId -> paragraphId 매핑)
        Map<Long, Long> quizParagraphMap = quizList.stream()
                .collect(Collectors.toMap(Quiz::getQuizId, Quiz::getParagraphId));

        return synonymQuizList.stream()
                .map(synonymQuiz -> QuizResponse.SynonymQuizDto.builder()
                        .quizId(synonymQuiz.getQuizId())
                        .paragraphId(quizParagraphMap.getOrDefault(synonymQuiz.getQuizId(), null)) // paragraphId 매핑
                        .word(synonymQuiz.getWord())
                        .sourceSentence(synonymQuiz.getSourceSentence())
                        .answer(synonymQuiz.getAnswer())
                        .type(QuizType.SYNONYM.getValue())
                        .explanation(synonymQuiz.getExplanation())
                        .option1(synonymQuiz.getOption1())
                        .option2(synonymQuiz.getOption2())
                        .option3(synonymQuiz.getOption3())
                        .option4(synonymQuiz.getOption4())
                        .build())
                .collect(Collectors.toList());
    }

    public static List<QuizResponse.MeaningQuizDto> toMeaningQuizDtoList(List<MeaningQuiz> meaningQuizList, List<Quiz> quizList) {
        Map<Long, Long> quizParagraphMap = quizList.stream()
                .collect(Collectors.toMap(Quiz::getQuizId, Quiz::getParagraphId));

        return meaningQuizList.stream()
                .map(meaningQuiz -> QuizResponse.MeaningQuizDto.builder()
                        .quizId(meaningQuiz.getQuizId())
                        .paragraphId(quizParagraphMap.getOrDefault(meaningQuiz.getQuizId(), null))
                        .word(meaningQuiz.getWord())
                        .sourceSentence(meaningQuiz.getSourceSentence())
                        .answer(meaningQuiz.getAnswer())
                        .type(QuizType.MEANING.getValue())
                        .explanation(meaningQuiz.getExplanation())
                        .option1(meaningQuiz.getOption1())
                        .option2(meaningQuiz.getOption2())
                        .option3(meaningQuiz.getOption3())
                        .option4(meaningQuiz.getOption4())
                        .build())
                .collect(Collectors.toList());
    }

    public static List<QuizResponse.ContentQuizDto> toContentQuizDtoList(List<ContentQuiz> contentQuizList, List<Quiz> quizList) {
        Map<Long, Long> quizParagraphMap = quizList.stream()
                .collect(Collectors.toMap(Quiz::getQuizId, Quiz::getParagraphId));
        return contentQuizList.stream()
                .map(contentQuiz -> QuizResponse.ContentQuizDto.builder()
                        .quizId(contentQuiz.getQuizId())
                        .paragraphId(quizParagraphMap.getOrDefault(contentQuiz.getQuizId(), null))
                        .answer(contentQuiz.getAnswer())
                        .question(contentQuiz.getQuestion())
                        .explanation(contentQuiz.getExplanation())
                        .type(QuizType.CONTENT.getValue())
                        .build())
                .collect(Collectors.toList());
    }
}
