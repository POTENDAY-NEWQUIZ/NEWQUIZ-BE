package com.example.newquiz.service;

import com.example.newquiz.common.exception.GeneralException;
import com.example.newquiz.common.status.ErrorStatus;
import com.example.newquiz.common.util.ClovaUtil;
import com.example.newquiz.domain.*;
import com.example.newquiz.infra.clova.dto.SummaryFeedbackClovaRequest;
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
    private final CompletedNewsRepository completedNewsRepository;
    private final ParagraphRepository paragraphRepository;
    private final QuizRepository quizRepository;
    private final QuizResultRepository quizResultRepository;
    private final UserRepository userRepository;
    private final RankingRepository rankingRepository;
    private final AISummaryService aiSummaryService;

    @Transactional
    public SummaryResponse.SummaryDto saveSummary(Long userId, SummaryRequest.SummaryDto summaryDto) {
        User user = getUserById(userId);
        CompletedNews completedNews = getCompletedNews(userId, summaryDto.getNewsId());
        List<Paragraph> paragraphs = paragraphRepository.findByNewsId(summaryDto.getNewsId());


        SummaryResponse.SummaryDto response = aiSummaryService.generateSummary(paragraphs, summaryDto.getParagraphs());
        completedNews.setSummaryScore(response.getTotalScore());
        completedNews.setIsCompleted(true);

        // 퀴즈 점수 계산 후 랭킹 반영
        int quizScore = calculateQuizScore(userId, summaryDto.getNewsId());
        updateRanking(userId, response.getTotalScore(), quizScore);

        // 연속 학습 일수 계산 및 저장
        int consecutiveDays = calculateConsecutiveLearningDays(userId);
        updateUserLearningStreak(user, consecutiveDays);

        // 평균 점수 업데이트
        updateUserAverageScore(user);

        return response;
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
     * 연속 학습 일수를 계산.
     */
    private int calculateConsecutiveLearningDays(Long userId) {
        List<CompletedNews> completedNewsList = completedNewsRepository.findByUserIdAndIsCompletedTrueOrderByUpdatedAtDesc(userId);
        int consecutiveDays = 0;
        LocalDate previousDate = LocalDate.now();

        for (CompletedNews news : completedNewsList) {
            LocalDate currentDate = news.getCreatedAt().toLocalDate();
            if (currentDate.equals(previousDate) || currentDate.equals(previousDate.minusDays(1))) {
                consecutiveDays++;
                previousDate = currentDate;
            } else {
                break;
            }
        }
        return consecutiveDays;
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

}
