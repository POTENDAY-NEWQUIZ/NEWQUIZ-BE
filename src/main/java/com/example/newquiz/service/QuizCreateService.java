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
    private final NewsRepository newsRepository;
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
        QuizCreateResponse response = getQuizResponse(request);

        if (response == null) {
            log.info(" 1 퀴즈 응답 파싱 실패로 인한 재시도...");
            response = getQuizResponse(request);
            if (response == null) {
                log.info("2 퀴즈 응답 파싱 실패로 인한 뉴스 삭제...");
                deleteNewsResource(newsId);
                throw new GeneralException(ErrorStatus.QUIZ_INVALID_AI_RESPONSE);
            }
        }

        List<Quiz> quizzes = createQuizFromResponse(response, newsId);
        if (quizzes == null) {
            log.info("3 퀴즈 타입 응답 실패로 인한 뉴스 삭제...");
            deleteNewsResource(newsId);
            throw new GeneralException(ErrorStatus.QUIZ_TYPE_INVALID_AI_RESPONSE);
        }

        if (!validateQuizzes(quizzes)) {
            log.info("4 퀴즈 유효성 검사 실패로 인한 재요청...");
            deleteQuizResource(quizzes);
            response = getQuizResponse(request);
            if (response == null) {
                log.info("5 퀴즈 응답 파싱 실패로 인한 뉴스 삭제...");
                deleteNewsResource(newsId);
                throw new GeneralException(ErrorStatus.QUIZ_INVALID_AI_RESPONSE);
            }
            quizzes = createQuizFromResponse(response, newsId);
            if (quizzes == null) {
                log.info("6 퀴즈 타입 응답 실패로 인한 뉴스 삭제...");
                deleteQuizResource(quizzes);
                deleteNewsResource(newsId);
                throw new GeneralException(ErrorStatus.QUIZ_TYPE_INVALID_AI_RESPONSE);
            } else if (!validateQuizzes(quizzes)) {
                log.info("7 퀴즈 유효성 검사 실패로 인한 뉴스 삭제...");
                deleteNewsResource(newsId);
                deleteQuizResource(quizzes);
                throw new GeneralException(ErrorStatus.QUIZ_INVALID_AI_RESPONSE);
            }
        }
        log.info("✅ 퀴즈 생성 완료 => 생성된 퀴즈 개수 : {}", quizzes.size());
    }

    protected void deleteNewsResource(Long newsId) {
        newsRepository.deleteById(newsId);
        paragraphRepository.deleteAllByNewsId(newsId);
    }

    protected void deleteQuizResource(List<Quiz> quizzes) {
        quizRepository.deleteAllByIdInBatch(quizzes.stream().map(Quiz::getQuizId).toList());
        deleteTypeQuiz(quizzes);
    }

    /**
     * Clova AI로부터 퀴즈 생성 요청을 보내고 응답을 파싱하는 메서드
     */
    private QuizCreateResponse getQuizResponse(QuizCreateClovaRequest request) {
        try {
            String responseJson = clovaUtil.postWebClient(request);
            return objectMapper.readValue(clovaUtil.parseContentFromResponse(responseJson), QuizCreateResponse.class);
        } catch (Exception e) {
            log.error("퀴즈 생성 응답 파싱 실패: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Clova AI 응답 데이터를 바탕으로 Quiz 엔티티 생성
     */
    protected List<Quiz> createQuizFromResponse(QuizCreateResponse response, Long newsId) {
        List<Quiz> quizzes = new ArrayList<>();

        for (QuizCreateResponse.Question question : response.getQuestions()) {
            QuizType quizType = checkQuizType(question.getType());
            if (quizType == null) {
                return null;
            }
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

    protected void deleteTypeQuiz(List<Quiz> quizzes) {
        for (Quiz quiz : quizzes) {
            Optional.ofNullable(quiz.getType())
                    .ifPresent(type -> {
                        switch (type) {
                            case SYNONYM:
                                synonymQuizRepository.deleteById(quiz.getSynonymQuizId());
                                break;
                            case MEANING:
                                meaningQuizRepository.deleteById(quiz.getMeaningQuizId());
                                break;
                            case CONTENT:
                                contentQuizRepository.deleteById(quiz.getContentQuizId());
                                break;
                        }
                    });
        }
    }

    private QuizType checkQuizType(String type) {
        QuizType quizType = QuizType.getQuizType(type);
        if (quizType == null) {
            return null;
        } else {
            return quizType;
        }
    }

    private boolean validateQuizzes(List<Quiz> quizzes) {
        return quizzes.stream().allMatch(this::isQuizValid);
    }

    private boolean isQuizValid(Quiz quiz) {
        switch (quiz.getType()) {
            case SYNONYM:
            case MEANING:
                return isAnswerInOptions(quiz) && isSelectedWordInSentence(quiz);
            default:
                return true;
        }
    }

    private boolean isAnswerInOptions(Quiz quiz) {
        switch (quiz.getType()) {
            case SYNONYM:
                return synonymQuizRepository.findById(quiz.getSynonymQuizId())
                        .map(synonymQuiz -> synonymQuiz.getOptions().contains(synonymQuiz.getOption(synonymQuiz.getAnswer() - 1)))
                        .orElse(false);
            case MEANING:
                return meaningQuizRepository.findById(quiz.getMeaningQuizId())
                        .map(meaningQuiz -> meaningQuiz.getOptions().contains(meaningQuiz.getOption(meaningQuiz.getAnswer() - 1)))
                        .orElse(false);
        }
        return false;
    }

    private boolean isSelectedWordInSentence(Quiz quiz) {
        switch (quiz.getType()) {
            case SYNONYM:
                return synonymQuizRepository.findById(quiz.getSynonymQuizId())
                        .map(synonymQuiz -> synonymQuiz.getSourceSentence().contains(synonymQuiz.getWord()))
                        .orElse(false);
            case MEANING:
                return meaningQuizRepository.findById(quiz.getMeaningQuizId())
                        .map(meaningQuiz -> meaningQuiz.getSourceSentence().contains(meaningQuiz.getWord()))
                        .orElse(false);
        }
        return false;
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
                                .sourceSentence(question.getSentence())
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
                                .sourceSentence(question.getSentence())
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