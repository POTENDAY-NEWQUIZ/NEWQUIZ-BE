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
    private final AntonymQuizRepository antonymQuizRepository;
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

        // 퀴즈 검증 및 재요청
        boolean isValid = validateQuizzes(quizzes);
        if (!isValid) {
            log.warn("퀴즈 검증 실패, 퀴즈를 다시 생성합니다. (뉴스 ID: {})", newsId);
            // 재요청
            response = parseQuizResponse(request);  // 재요청
            if (response != null) {
                quizRepository.deleteAll(quizzes);  // 기존 퀴즈 삭제
                deleteTypeQuiz(quizzes); // 유형별 퀴즈도 삭제
                quizzes = createQuizFromResponse(response, newsId);  // 재요청
                isValid = validateQuizzes(quizzes);  // 검증
            }
        }

        // 퀴즈가 여전히 유효하지 않으면 해당 뉴스와 문단 삭제
        if (!isValid) {
            log.error("퀴즈 생성 실패: 조건을 만족하지 않음. 뉴스 ID: {}", newsId);
            quizRepository.deleteAll(quizzes);  // 퀴즈 삭제
            deleteTypeQuiz(quizzes); // 유형별 퀴즈도 삭제
            throw new GeneralException(ErrorStatus.INVALID_AI_RESPONSE);
        }

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

            case ANTONYM:
                AntonymQuiz antonymQuiz = antonymQuizRepository.save(
                        AntonymQuiz.builder()
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
                quiz.setAntonymQuizId(antonymQuiz.getAntonymQuizId());
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

    /**
     * 퀴즈의 답이 옵션에 포함되어 있는지, selectedWord가 selectedSentence에 포함되어 있는지 검증
     */
    private boolean validateQuizzes(List<Quiz> quizzes) {
        for (Quiz quiz : quizzes) {
            if (quiz.getType() == QuizType.SYNONYM || quiz.getType() == QuizType.ANTONYM || quiz.getType() == QuizType.MEANING) {
                // 각 퀴즈 유형에서 answer가 options에 포함되어 있는지 확인
                if (!isAnswerInOptions(quiz)) {
                    log.warn("퀴즈의 답이 옵션에 포함되지 않음. 퀴즈 ID: {}", quiz.getQuizId());
                    return false;
                }
            }

            if (quiz.getType() == QuizType.SYNONYM || quiz.getType() == QuizType.ANTONYM || quiz.getType() == QuizType.MEANING) {
                // selectedWord가 selectedSentence에 포함되어 있는지 확인
                if (!isSelectedWordInSentence(quiz)) {
                    log.warn("퀴즈의 selectedWord가 selectedSentence에 포함되지 않음. 퀴즈 ID: {}", quiz.getQuizId());
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isAnswerInOptions(Quiz quiz) {
        switch (quiz.getType()) {
            case SYNONYM:
                return synonymQuizRepository.findById(quiz.getSynonymQuizId())
                        .map(synonymQuiz -> synonymQuiz.getOptions().contains(synonymQuiz.getAnswer()))
                        .orElse(false);
            case ANTONYM:
                return antonymQuizRepository.findById(quiz.getAntonymQuizId())
                        .map(antonymQuiz -> antonymQuiz.getOptions().contains(antonymQuiz.getAnswer()))
                        .orElse(false);
            case MEANING:
                return meaningQuizRepository.findById(quiz.getMeaningQuizId())
                        .map(meaningQuiz -> meaningQuiz.getOptions().contains(meaningQuiz.getAnswer()))
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
            case ANTONYM:
                return antonymQuizRepository.findById(quiz.getAntonymQuizId())
                        .map(antonymQuiz -> antonymQuiz.getSourceSentence().contains(antonymQuiz.getWord()))
                        .orElse(false);
            case MEANING:
                return meaningQuizRepository.findById(quiz.getMeaningQuizId())
                        .map(meaningQuiz -> meaningQuiz.getSourceSentence().contains(meaningQuiz.getWord()))
                        .orElse(false);
        }
        return false;
    }

    private void deleteTypeQuiz(List<Quiz> quizzes) {
        for (Quiz quiz : quizzes) {
            switch (quiz.getType()) {
                case SYNONYM:
                    synonymQuizRepository.deleteById(quiz.getSynonymQuizId());
                    break;
                case ANTONYM:
                    antonymQuizRepository.deleteById(quiz.getAntonymQuizId());
                    break;
                case MEANING:
                    meaningQuizRepository.deleteById(quiz.getMeaningQuizId());
                    break;
                case CONTENT:
                    contentQuizRepository.deleteById(quiz.getContentQuizId());
                    break;
            }
        }
    }
}
