package com.example.newquiz.service;

import com.example.newquiz.common.exception.GeneralException;
import com.example.newquiz.common.status.ErrorStatus;
import com.example.newquiz.common.util.ImageUtil;
import com.example.newquiz.common.util.JwtUtil;
import com.example.newquiz.domain.CompletedNews;
import com.example.newquiz.domain.Ranking;
import com.example.newquiz.domain.RefreshToken;
import com.example.newquiz.domain.User;
import com.example.newquiz.dto.converter.UserConverter;
import com.example.newquiz.dto.request.UserRequest;
import com.example.newquiz.dto.response.UserResponse;
import com.example.newquiz.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RankingRepository rankingRepository;
    private final CompletedNewsRepository completedNewsRepository;
    private final QuizResultRepository quizResultRepository;
    private final ImageUtil imageUtil;

    private final String DEFAULT_IMAGE = "https://newquiz-s3-bucket.s3.ap-northeast-2.amazonaws.com/profile/newquiz-default-profile.jpg";


    // 회원가입
    @Transactional
    public UserResponse.UserDto registerUser(String registerToken, UserRequest.UserRegisterDto userRegisterDto) {
        // 레지스터 토큰 유효성 확인
        String providerId = checkRegisterToken(registerToken);

        // 이미 가입된 providerId인지 확인
        checkProviderId(providerId);

        // User 생성
        User user = User.toEntity(providerId, userRegisterDto.getNickName(), userRegisterDto.getBirth());

        // User 저장
        userRepository.save(user);

        // AccessToken, RefreshToken 발급
        String accessToken = jwtUtil.generateAccessToken(user.getUserId().toString());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserId().toString());

        // RefreshToken 생성 후 저장
        RefreshToken newRefreshToken = RefreshToken.toEntity(refreshToken, user.getUserId());
        refreshTokenRepository.save(newRefreshToken);

        // 랭킹 생성
        Ranking ranking = Ranking.toEntity(user.getUserId());
        rankingRepository.save(ranking);

        return UserConverter.toUserDto(user.getUserId(), user.getNickName(), accessToken, refreshToken);
    }

    private String checkRegisterToken(String registerToken) {
        jwtUtil.validateToken(registerToken);
        return jwtUtil.getProviderIdFromToken(registerToken);
    }

    private void checkProviderId(String providerId) {
        if (userRepository.existsByProviderId(providerId)) {
            throw new GeneralException(ErrorStatus.ALREADY_REGISTERED_USER);
        }
    }

    // 닉네임 중복 체크
    public UserResponse.NickNameCheckDto checkNickname(String nickname) {
        Boolean isDuplicate = userRepository.existsByNickName(nickname);
        return UserConverter.toNickNameCheckDto(isDuplicate);
    }

    // 마이페이지 정보 조회
    public UserResponse.MyPageDto getMyPageInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND_USER_BY_USER_ID));

        List<LocalDate> calendar = calculateConsecutiveLearningDays(userId);
        int learningDays = calendar == null ? 0 : calculateLearningDays(calendar.get(0), calendar.get(1));
        int userQuizCount = completedNewsRepository.countByUserIdAndIsCompletedTrue(userId);

        return UserConverter.toMyPageDto(user, learningDays, userQuizCount);
    }

    // 닉네임 변경
    @Transactional
    public void changeNickname(Long userId, String nickname) {
        User user = userRepository.findById(userId).orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND_USER_BY_USER_ID));
        user.setNickName(nickname);
    }

    // 로그아웃
    @Transactional
    public void logout(String refreshToken) {
        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findByRefreshToken(refreshToken);
        refreshTokenOptional.ifPresent(refreshTokenRepository::delete);
    }

    // 회원 탈퇴
    @Transactional
    public void deleteUser(Long userId, String refreshToken) {
        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findByRefreshToken(refreshToken);
        refreshTokenOptional.ifPresent(refreshTokenRepository::delete);
        // 연관된 데이터 삭제
        userRepository.deleteById(userId);
        completedNewsRepository.deleteByUserId(userId);
        quizResultRepository.deleteByUserId(userId);
        rankingRepository.deleteByUserId(userId);
    }

    // 회원 사진 변경
    @Transactional
    public UserResponse.ProfileImageDto changeProfile(Long userId, MultipartFile profileImage) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND_USER_BY_USER_ID));
        String originalProfileImageUrl = user.getProfileImageUrl();

        user.setProfileImageUrl(imageUtil.saveFile(profileImage, "profile"));
        userRepository.save(user);

        // 기존 이미지는 삭제
        if (!originalProfileImageUrl.equals(DEFAULT_IMAGE)) {
            imageUtil.deleteImage(originalProfileImageUrl);
        }
        return UserResponse.ProfileImageDto.builder().profileImageUrl(user.getProfileImageUrl()).build();
    }

    // 회원 사진 삭제
    @Transactional
    public UserResponse.ProfileImageDto deleteProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND_USER_BY_USER_ID));
        String originalProfileImageUrl = user.getProfileImageUrl();

        if (DEFAULT_IMAGE.equals(originalProfileImageUrl)) {
            throw new GeneralException(ErrorStatus.DEFAULT_PROFILE_IMAGE);
        }

        user.setProfileImageUrl(DEFAULT_IMAGE); // 기본 이미지 URL로 설정
        userRepository.save(user);

        // 기존 이미지는 삭제
        if (originalProfileImageUrl != null) {
            imageUtil.deleteImage(originalProfileImageUrl);
        }
        return UserResponse.ProfileImageDto.builder().profileImageUrl(user.getProfileImageUrl()).build();
    }

    // 학습 정보 조회
    public UserResponse.UserStudyInfoDto getUserStudyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND_USER_BY_USER_ID));

        // 연속 학습한 날짜 범위 계산
        List<LocalDate> calendar = calculateConsecutiveLearningDays(userId);
        int learningDays = (calendar == null || calendar.isEmpty()) ? 0 : calculateLearningDays(calendar.get(0), calendar.get(1));

        // 현재 날짜를 기준으로 일주일 동안 각 일마다 CompletedNews 가 true 인 것들 개수 가져오기
        LocalDate oneWeekAgo = LocalDate.now().minusDays(6);
        List<Object[]> learningCountList = completedNewsRepository.countByUserIdAndIsCompletedTrueGroupByDate(userId, oneWeekAgo);

        Map<LocalDate, Integer> graphMap = toGraphMap(learningCountList);

        // 7일치 데이터 보정 (중간에 학습 기록이 없는 날짜는 0으로 채움)
        List<UserResponse.GraphDto> graphData = new ArrayList<>();
        for (LocalDate date = oneWeekAgo; !date.isAfter(LocalDate.now()); date = date.plusDays(1)) { // 오늘 포함
            String dayOfWeek = convertDayOfWeek(date.getDayOfWeek().getValue()); // 요일 변환
            graphData.add(new UserResponse.GraphDto(date, dayOfWeek, graphMap.getOrDefault(date, 0)));
        }

        int totalCount = graphMap.values().stream().mapToInt(Integer::intValue).sum();
        int userQuizCount = completedNewsRepository.countByUserIdAndIsCompletedTrue(userId);

        return UserConverter.toUserStudyInfoDto(user, userQuizCount, calendar == null || calendar.isEmpty() ? null : calendar.get(0), calendar == null || calendar.isEmpty() ? null : calendar.get(1), learningDays, totalCount, graphData);
    }

    private static Map<LocalDate, Integer> toGraphMap(List<Object[]> learningCountList) {
        return learningCountList.stream()
                .collect(Collectors.toMap(
                        result -> ((java.sql.Date) result[0]).toLocalDate(),
                        result -> ((Number) result[1]).intValue(),
                        (existing, replacement) -> existing // 중복 방지
                ));
    }

    private String convertDayOfWeek(int dayValue) {
        switch (dayValue) {
            case 1: return "월";
            case 2: return "화";
            case 3: return "수";
            case 4: return "목";
            case 5: return "금";
            case 6: return "토";
            case 7: return "일";
            default: return "";
        }
    }

    /**
     * 연속 학습한 날짜 범위를 계산
     */
    public List<LocalDate> calculateConsecutiveLearningDays(Long userId) {
        List<CompletedNews> completedNewsList = completedNewsRepository.findByUserIdAndIsCompletedTrueOrderByUpdatedAtDesc(userId);

        if (completedNewsList.isEmpty()) {
            return null; // 학습 기록이 없으면 null 반환
        }

        // 가장 최신 학습일을 추출
        LocalDate latestLearningDate = completedNewsList.get(0).getUpdatedAt().toLocalDate();

        // 최신 학습일이 오늘 또는 어제가 아닌 경우 연속 학습 일수 계산을 하지 않음
        LocalDate today = LocalDate.now();
        if (!latestLearningDate.equals(today) && !latestLearningDate.equals(today.minusDays(1))) {
            return null;
        }

        // 날짜만 추출하여 정렬된 리스트 생성
        List<LocalDate> dates = completedNewsList.stream()
                .map(news -> news.getUpdatedAt().toLocalDate())
                .distinct() // 중복 제거
                .sorted(Comparator.reverseOrder()) // 최신 날짜부터 정렬
                .toList();

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
