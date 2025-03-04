package com.example.newquiz.dto.converter;

import com.example.newquiz.domain.*;
import com.example.newquiz.domain.enums.QuizType;
import com.example.newquiz.dto.response.QuizResponse;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QuizConverter {
    public static QuizResponse.QuizListDto toQuizListDto(List<Long> quizIds, int synonymQuizCount, int antonymQuizCount, int meaningQuizCount, int contentQuizCount, List<QuizResponse.SynonymQuizDto> synonymQuizDtoList, List<QuizResponse.MeaningQuizDto> meaningQuizDtoList, List<QuizResponse.ContentQuizDto> contentQuizDtoList, List<QuizResponse.AntonymQuizDto> antonymQuizDtoList) {
        return QuizResponse.QuizListDto.builder()
                .totalQuizCount(synonymQuizCount + meaningQuizCount + contentQuizCount + antonymQuizCount)
                .synonymQuizCount(synonymQuizCount)
                .antonymQuizCount(antonymQuizCount)
                .meaningQuizCount(meaningQuizCount)
                .contentQuizCount(contentQuizCount)
                .quizIdList(quizIds)
                .synonymQuiz(synonymQuizDtoList)
                .meaningQuiz(meaningQuizDtoList)
                .contentQuiz(contentQuizDtoList)
                .antonymQuiz(antonymQuizDtoList)
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

    public static List<QuizResponse.AntonymQuizDto> antonymQuizDtoList(List<AntonymQuiz> antonymQuizList, List<Quiz> quizList) {
        // Quiz 리스트를 Map으로 변환 (quizId -> paragraphId 매핑)
        Map<Long, Long> quizParagraphMap = quizList.stream()
                .collect(Collectors.toMap(Quiz::getQuizId, Quiz::getParagraphId));

        return antonymQuizList.stream()
                .map(antonymQuiz -> QuizResponse.AntonymQuizDto.builder()
                        .quizId(antonymQuiz.getQuizId())
                        .paragraphId(quizParagraphMap.getOrDefault(antonymQuiz.getQuizId(), null)) // paragraphId 매핑
                        .word(antonymQuiz.getWord())
                        .sourceSentence(antonymQuiz.getSourceSentence())
                        .answer(antonymQuiz.getAnswer())
                        .type(QuizType.ANTONYM.getValue())
                        .explanation(antonymQuiz.getExplanation())
                        .option1(antonymQuiz.getOption1())
                        .option2(antonymQuiz.getOption2())
                        .option3(antonymQuiz.getOption3())
                        .option4(antonymQuiz.getOption4())
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
