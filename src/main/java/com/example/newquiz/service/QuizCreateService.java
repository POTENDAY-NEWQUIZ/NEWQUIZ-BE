package com.example.newquiz.service;

import com.example.newquiz.common.exception.GeneralException;
import com.example.newquiz.common.status.ErrorStatus;
import com.example.newquiz.common.util.ClovaUtil;
import com.example.newquiz.domain.*;
import com.example.newquiz.domain.enums.QuizType;
import com.example.newquiz.dto.request.QuizCreateClovaRequest;
import com.example.newquiz.dto.response.QuizCreateResponse;
import com.example.newquiz.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizCreateService {

    private final ClovaUtil clovaUtil;
    private final ParagraphRepository paragraphRepository;
    private final QuizRepository quizRepository;
    private final SynonymQuizRepository synonymQuizRepository;
    private final MeaningQuizRepository meaningQuizRepository;
    private final ContentQuizRepository contentQuizRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void createQuiz(Long newsId) {
        List<Paragraph> paragraphs = paragraphRepository.findByNewsIdOrderByContentOrderAsc(newsId);
        if (paragraphs.isEmpty()) {
            log.warn("뉴스 ID {}에 대한 문단이 없습니다.", newsId);
            return;
        }

        // Clova AI 요청 및 응답 파싱
        QuizCreateClovaRequest request = QuizCreateClovaRequest.createQuizCreateClovaRequest(paragraphs);
        QuizCreateResponse response = parseQuizResponse(request);
        if (response == null) return;

        // 퀴즈 저장
        List<Quiz> quizzes = createQuizFromResponse(response, newsId);
        quizRepository.saveAll(quizzes);
        log.info("뉴스 ID {}에 대한 퀴즈 {}개 생성 완료", newsId, quizzes.size());
    }

    /**
     * Clova AI로부터 퀴즈 생성 요청을 보내고 응답을 파싱하는 메서드
     */
    private QuizCreateResponse parseQuizResponse(QuizCreateClovaRequest request) {
        try {
            String responseJson = clovaUtil.postWebClient(request);
            return objectMapper.readValue(clovaUtil.parseContentFromResponse(responseJson), QuizCreateResponse.class);
        } catch (Exception e) {
            log.error("퀴즈 생성 응답 파싱 실패: {}", e.getMessage());
            throw new GeneralException(ErrorStatus.INVALID_AI_RESPONSE);
        }
    }

    /**
     * Clova AI 응답 데이터를 바탕으로 Quiz 엔티티 생성
     */
    private List<Quiz> createQuizFromResponse(QuizCreateResponse response, Long newsId) {
        List<Quiz> quizzes = new ArrayList<>();

        for (QuizCreateResponse.Question question : response.getQuestions()) {
            QuizType quizType = QuizType.getQuizType(question.getType());

            Quiz quiz = quizRepository.save(
                    Quiz.builder()
                            .newsId(newsId)
                            .paragraphId(Long.valueOf(question.getSourceParagraphId()))
                            .type(quizType)
                            .build()
            );

            saveQuizByType(quiz, question, quizType);
            quizzes.add(quiz);
        }
        return quizzes;
    }

    /**
     * 퀴즈 타입에 따라 적절한 엔티티 저장
     */
    private void saveQuizByType(Quiz quiz, QuizCreateResponse.Question question, QuizType quizType) {
        switch (quizType) {
            case SYNONYM:
                SynonymQuiz synonymQuiz = synonymQuizRepository.save(
                        SynonymQuiz.builder()
                                .quizId(quiz.getQuizId())
                                .answer(getAnswerIndex(question.getOptions(), question.getAnswer()))
                                .word(question.getSelectedWord())
                                .option1(question.getOptions().get(0))
                                .option2(question.getOptions().get(1))
                                .option3(question.getOptions().get(2))
                                .option4(question.getOptions().get(3))
                                .explanation(question.getExplanation())
                                .sourceSentence(question.getSourceSentence())
                                .example(question.getExample())
                                .build()
                );
                quiz.setSynonymQuizId(synonymQuiz.getSynonymQuizId());
                break;

            case MEANING:
                MeaningQuiz meaningQuiz = meaningQuizRepository.save(
                        MeaningQuiz.builder()
                                .quizId(quiz.getQuizId())
                                .answer(getAnswerIndex(question.getOptions(), question.getAnswer()))
                                .option1(question.getOptions().get(0))
                                .option2(question.getOptions().get(1))
                                .option3(question.getOptions().get(2))
                                .option4(question.getOptions().get(3))
                                .explanation(question.getExplanation())
                                .sourceSentence(question.getSourceSentence())
                                .word(question.getSelectedWord())
                                .example(question.getExample())
                                .build()
                );
                quiz.setMeaningQuizId(meaningQuiz.getMeaningQuizId());
                break;

            case CONTENT:
                ContentQuiz contentQuiz = contentQuizRepository.save(
                        ContentQuiz.builder()
                                .quizId(quiz.getQuizId())
                                .answer(Boolean.parseBoolean(question.getAnswer()))
                                .explanation(question.getExplanation())
                                .question(question.getQuestion())
                                .build()
                );
                quiz.setContentQuizId(contentQuiz.getContentQuizId());
                break;
        }
    }

    /**
     * 정답이 어떤 옵션인지 인덱스를 찾아 반환 (1-based index)
     */
    private int getAnswerIndex(List<String> options, String answer) {
        return options.indexOf(answer) + 1;
    }
}
