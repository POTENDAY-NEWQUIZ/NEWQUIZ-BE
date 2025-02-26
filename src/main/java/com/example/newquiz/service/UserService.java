package com.example.newquiz.service;

import com.example.newquiz.common.exception.GeneralException;
import com.example.newquiz.common.status.ErrorStatus;
import com.example.newquiz.common.util.JwtUtil;
import com.example.newquiz.domain.Ranking;
import com.example.newquiz.domain.RefreshToken;
import com.example.newquiz.domain.User;
import com.example.newquiz.dto.converter.UserConverter;
import com.example.newquiz.dto.request.UserRequest;
import com.example.newquiz.dto.response.UserResponse;
import com.example.newquiz.repository.RankingRepository;
import com.example.newquiz.repository.RefreshTokenRepository;
import com.example.newquiz.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RankingRepository rankingRepository;

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
}
