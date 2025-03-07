package com.example.newquiz.service;

import com.example.newquiz.common.exception.GeneralException;
import com.example.newquiz.common.status.ErrorStatus;
import com.example.newquiz.common.util.ImageUtil;
import com.example.newquiz.common.util.JwtUtil;
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
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RankingRepository rankingRepository;
    private final HomeService homeService;
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

        List<LocalDate> calendar = homeService.calculateConsecutiveLearningDays(userId);
        int learningDays = calendar == null ? 0 : homeService.calculateLearningDays(calendar.get(0), calendar.get(1));
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
}
