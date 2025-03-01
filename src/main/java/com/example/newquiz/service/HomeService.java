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
import java.util.Comparator;
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
            return null; // 학습 기록이 없으면 null 반환
        }

        // 날짜만 추출하여 정렬된 리스트 생성
        List<LocalDate> dates = completedNewsList.stream()
                .map(news -> news.getUpdatedAt().toLocalDate())
                .distinct() // 중복 제거
                .sorted(Comparator.reverseOrder()) // 최신 날짜부터 정렬
                .toList();

        LocalDate today = LocalDate.now();
        LocalDate startDate;
        LocalDate endDate;

        // 오늘 학습한 기록이 있는지 확인
        boolean hasTodayRecord = dates.contains(today);

        if (hasTodayRecord) {
            endDate = today; // 오늘 학습했으면 오늘을 기준으로
        } else {
            endDate = today.minusDays(1); // 오늘 학습 안 했으면 어제를 기준으로
        }

        startDate = endDate;

        // 연속된 날짜를 찾기
        for (LocalDate date : dates) {
            if (date.equals(startDate) || date.equals(startDate.minusDays(1))) {
                startDate = date; // 연속 학습일 업데이트
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