package com.example.newquiz.controller;

import com.example.newquiz.auth.dto.CustomUserDetails;
import com.example.newquiz.common.response.ApiResponse;
import com.example.newquiz.common.status.SuccessStatus;
import com.example.newquiz.dto.request.UserRequest;
import com.example.newquiz.dto.response.UserResponse;
import com.example.newquiz.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse.UserDto>> registerUser(
            @RequestHeader("registerToken") String registerToken,
            @RequestBody UserRequest.UserRegisterDto userRegisterDto
    ) {
        UserResponse.UserDto registerResponse = userService.registerUser(registerToken, userRegisterDto);

        return ApiResponse.success(SuccessStatus.USER_REGISTER_SUCCESS, registerResponse);
    }

    @PostMapping("/nickname/check")
    public ResponseEntity<ApiResponse<UserResponse.NickNameCheckDto>> checkNickname(
            @RequestBody UserRequest.NickNameCheckDto nickNameCheckDto
    ) {
        UserResponse.NickNameCheckDto isExist = userService.checkNickname(nickNameCheckDto.getNickName());

        return ApiResponse.success(SuccessStatus.OK, isExist);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<UserResponse.MyPageDto>> getUserInfo(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
            ) {
        UserResponse.MyPageDto myPageDto = userService.getMyPageInfo(customUserDetails.getUserId());

        return ApiResponse.success(SuccessStatus.GET_MYPAGE_INFO_SUCCESS , myPageDto);
    }

    @PatchMapping("/nickname")
    public ResponseEntity<ApiResponse<String>> changeNickname(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody UserRequest.NickNameChangeDto nickNameChangeDto
    ) {
        userService.changeNickname(customUserDetails.getUserId(), nickNameChangeDto.getNickName());
        return ApiResponse.success(SuccessStatus.CHANGE_NICKNAME_SUCCESS);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            @RequestHeader("refreshToken") String refreshToken
    ) {
        userService.logout(refreshToken);
        return ApiResponse.success(SuccessStatus.LOGOUT_SUCCESS);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> deleteUser(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestHeader("refreshToken") String refreshToken
    ) {
        userService.deleteUser(customUserDetails.getUserId(), refreshToken);
        return ApiResponse.success(SuccessStatus.DELETE_USER_SUCCESS);
    }

    @PatchMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse.ProfileImageDto>> changeProfile(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestPart(value = "profile") MultipartFile profileImage
    ) throws IOException {
        UserResponse.ProfileImageDto profileImageDto = userService.changeProfile(customUserDetails.getUserId(), profileImage);

        return ApiResponse.success(SuccessStatus.CHANGE_PROFILE_SUCCESS, profileImageDto);
    }

    @DeleteMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse.ProfileImageDto>> deleteProfile(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        UserResponse.ProfileImageDto profileImageDto = userService.deleteProfile(customUserDetails.getUserId());
        return ApiResponse.success(SuccessStatus.DELETE_PROFILE_SUCCESS, profileImageDto);
    }

    @GetMapping("/study")
    public ResponseEntity<ApiResponse<UserResponse.UserStudyInfoDto>> getUserStudyInfo(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        UserResponse.UserStudyInfoDto userStudyInfoDto = userService.getUserStudyInfo(customUserDetails.getUserId());
        return ApiResponse.success(SuccessStatus.USER_STUDY_INFO_SUCCESS,  userStudyInfoDto);
    }


}
