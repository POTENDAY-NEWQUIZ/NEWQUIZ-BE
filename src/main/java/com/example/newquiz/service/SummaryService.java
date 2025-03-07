package com.example.newquiz.service;

import com.example.newquiz.common.exception.GeneralException;
import com.example.newquiz.common.status.ErrorStatus;
import com.example.newquiz.common.util.ClovaUtil;
import com.example.newquiz.domain.*;
import com.example.newquiz.dto.converter.SummaryConverter;
import com.example.newquiz.dto.request.SummaryFeedbackClovaRequest;
import com.example.newquiz.dto.request.SummaryRequest;
import com.example.newquiz.dto.response.SummaryResponse;
import com.example.newquiz.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SummaryService {
    private final ClovaUtil clovaUtil;
    private final CompletedNewsRepository completedNewsRepository;
    private final ParagraphRepository paragraphRepository;
    private final QuizRepository quizRepository;
    private final QuizResultRepository quizResultRepository;
    private final UserRepository userRepository;
    private final RankingRepository rankingRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserService userService;

    @Transactional
    public SummaryResponse.SummaryFeedback saveSummary(Long userId, SummaryRequest.SummaryDto summaryDto) {
        User user = getUserById(userId);
        CompletedNews completedNews = getCompletedNews(userId, summaryDto.getNewsId());
        List<Paragraph> paragraphs = paragraphRepository.findByNewsId(summaryDto.getNewsId());

        // Clova AI 요약 분석 요청 및 결과 저장
        SummaryResponse.SummaryDto response = parseSummaryResponse(
                SummaryFeedbackClovaRequest.createSummaryFeedbackClovaRequest(paragraphs, summaryDto.getParagraphs())
        );
        completedNews.setSummaryScore(response.getTotalScore());
        completedNews.setIsCompleted(true);

        // 퀴즈 점수 계산 후 랭킹 반영
        int quizScore = calculateQuizScore(userId, summaryDto.getNewsId());
        updateRanking(userId, response.getTotalScore(), quizScore);

        // 연속 학습 일수 계산 및 저장
        List<LocalDate> calendar = userService.calculateConsecutiveLearningDays(userId);
        int learningDays = calendar == null ? 0 : userService.calculateLearningDays(calendar.get(0), calendar.get(1));
        updateUserLearningStreak(user, learningDays);

        // 평균 점수 업데이트
        updateUserAverageScore(user);

        return SummaryConverter.convertToSummaryFeedback(response, paragraphs);
    }

    /**
     * 사용자 ID로 User 객체를 가져오기
     */
    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND_USER_BY_USER_ID));
    }

    /**
     * 특정 유저가 완료한 뉴스 정보를 가져오기.
     */
    private CompletedNews getCompletedNews(Long userId, Long newsId) {
        Optional<CompletedNews> complagedNews =  completedNewsRepository.findByUserIdAndNewsId(userId, newsId);
        if (complagedNews.isPresent()) {
            return complagedNews.get();
        } else {
            throw new GeneralException(ErrorStatus.NOT_FOUND_COMPLETED_NEWS);
        }
    }

    /**
     * 퀴즈 점수 계산.
     */
    private int calculateQuizScore(Long userId, Long newsId) {
        return (int) quizRepository.findByNewsId(newsId).stream()
                .filter(quiz -> quizResultRepository.existsByUserIdAndQuizIdAndIsCorrect(userId, quiz.getQuizId(), true))
                .count();
    }

    /**
     * 랭킹 업데이트.
     */
    private void updateRanking(Long userId, int summaryScore, int quizScore) {
        Ranking ranking = rankingRepository.findByUserId(userId);
        ranking.setScore(ranking.getScore() + summaryScore + quizScore);
    }

    /**
     * 사용자의 연속 학습 일수를 업데이트.
     */
    private void updateUserLearningStreak(User user, int consecutiveDays) {
        if (user.getMaxLearningDays() == null || consecutiveDays > user.getMaxLearningDays()) {
            user.setMaxLearningDays(consecutiveDays);
        }
    }

    /**
     * 사용자의 평균 점수를 계산하고 업데이트.
     */
    private void updateUserAverageScore(User user) {
        List<CompletedNews> completedNewsList = completedNewsRepository.findByUserIdAndIsCompletedTrueOrderByUpdatedAtDesc(user.getUserId());

        if (!completedNewsList.isEmpty()) {
            double avgScore = Math.round(
                    (completedNewsList.stream()
                            .mapToInt(CompletedNews::getSummaryScore)
                            .average()
                            .orElse(0.0)) * 100.0
            ) / 100.0;

            user.setAvgScore(avgScore);
            if (user.getMaxAvgScore() == null || avgScore > user.getMaxAvgScore()) {
                user.setMaxAvgScore(avgScore);
            }
        }
    }

    /**
     * Clova AI API를 호출하여 요약 결과를 가져옴.
     */
    private SummaryResponse.SummaryDto parseSummaryResponse(SummaryFeedbackClovaRequest request) {
        try {
            String responseJson = clovaUtil.postWebClient(request);
            log.info("요약 응답: {}", responseJson);
            return objectMapper.readValue(clovaUtil.parseContentFromResponse(responseJson), SummaryResponse.SummaryDto.class);
        } catch (Exception e) {
            log.error("요약 응답 파싱 실패: {}", e.getMessage());
            throw new GeneralException(ErrorStatus.INVALID_AI_RESPONSE);
        }
    }
}
