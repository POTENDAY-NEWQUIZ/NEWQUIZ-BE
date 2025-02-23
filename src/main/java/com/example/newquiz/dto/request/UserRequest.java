package com.example.newquiz.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

public class UserRequest {
    @Data
    public static class UserRegisterDto {
        @NotBlank(message = "닉네임을 입력해주세요.")
        private String nickName;
        @NotBlank(message = "생년월일을 입력해주세요.")
        private LocalDate birth;
    }

    @Data
    public static class NickNameCheckDto {
        @NotBlank(message = "닉네임을 입력해주세요.")
        private String nickName;
    }
}
