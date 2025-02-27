package com.example.newquiz.service;

import com.example.newquiz.common.exception.GeneralException;
import com.example.newquiz.common.status.ErrorStatus;
import com.example.newquiz.domain.CompletedNews;
import com.example.newquiz.domain.User;
import com.example.newquiz.dto.response.UserResponse;
import com.example.newquiz.repository.CompletedNewsRepository;
import com.example.newquiz.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HomeService {
    private final UserRepository userRepository;
    private final CompletedNewsRepository completedNewsRepository;

    public UserResponse.HomeInfoDto getHomeInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND_USER_BY_USER_ID));

        // 연속 학습한 날짜 범위 계산
        List<LocalDate> calendar = calculateConsecutiveLearningDays(userId);
        int learningDays = calendar == null ? 0 : calculateLearningDays(calendar.get(0), calendar.get(1));

        return UserResponse.HomeInfoDto.builder()
                .startDate(calendar == null ? null : calendar.get(0))
                .endDate(calendar == null ? null : calendar.get(1))
                .learningDays(learningDays)
                .maxLearningDays(user.getMaxLearningDays())
                .build();
    }

    /**
     * 연속 학습한 날짜 범위를 계산
     */
    public List<LocalDate> calculateConsecutiveLearningDays(Long userId) {
        List<CompletedNews> completedNewsList = completedNewsRepository.findByUserIdAndIsCompletedTrueOrderByUpdatedAtDesc(userId);

        if (completedNewsList.isEmpty()) {
            return null; // 학습이 없으면 null 반환
        }

        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);

        // 현재 월에 학습한 기록이 있는지 확인
        boolean hasLearningThisMonth = completedNewsList.stream()
                .map(news -> news.getUpdatedAt().toLocalDate())
                .anyMatch(date -> YearMonth.from(date).equals(currentMonth));

        if (!hasLearningThisMonth) {
            return null; // 이번 달 학습 기록이 없으면 null 반환
        }

        // 오늘 학습했는지 확인
        Optional<LocalDate> latestStudyDate = completedNewsList.stream()
                .map(news -> news.getUpdatedAt().toLocalDate())
                .filter(date -> date.equals(today))
                .findFirst();

        LocalDate current = latestStudyDate.orElse(today.minusDays(1)); // 오늘 학습 안 했으면 어제부터 시작
        LocalDate startDate = current;
        LocalDate endDate = current;

        for (CompletedNews news : completedNewsList) {
            LocalDate newsDate = news.getUpdatedAt().toLocalDate();
            if (newsDate.equals(current) || newsDate.equals(current.minusDays(1))) {
                startDate = newsDate;
                current = newsDate.minusDays(1);
            } else {
                break; // 연속되지 않는 날짜가 나오면 종료
            }
        }

        return List.of(startDate, endDate);
    }

    public int calculateLearningDays(LocalDate startDate, LocalDate endDate) {
        return (int) startDate.until(endDate).getDays() + 1;
    }
}