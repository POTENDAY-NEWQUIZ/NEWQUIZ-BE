package com.example.newquiz.controller;

import com.example.newquiz.common.response.ApiResponse;
import com.example.newquiz.common.status.SuccessStatus;
import com.example.newquiz.dto.request.UserRequest;
import com.example.newquiz.dto.response.UserResponse;
import com.example.newquiz.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/nickname/check")
    public ResponseEntity<ApiResponse<UserResponse.NickNameCheckDto>> checkNickname(
            @RequestBody UserRequest.NickNameCheckDto nickNameCheckDto
    ) {
        UserResponse.NickNameCheckDto isExist = userService.checkNickname(nickNameCheckDto.getNickName());

        return ApiResponse.success(SuccessStatus.OK, isExist);
    }

}
