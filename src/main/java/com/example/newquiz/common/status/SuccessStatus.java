package com.example.newquiz.common.status;

import com.example.newquiz.common.base.status.BaseSuccessStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseSuccessStatus {
    OK(HttpStatus.OK, 200, "성공적으로 요청되었습니다."),
    CREATED(HttpStatus.CREATED, 201, "성공적으로 생성되었습니다."),
    NO_CONTENT(HttpStatus.NO_CONTENT, 204, "성공적으로 삭제되었습니다."),

    // 회원 관련 성공
    USER_REGISTER_SUCCESS(HttpStatus.CREATED, 201, "회원가입에 성공하였습니다."),

    // 토큰 관련 성공
    TOKEN_REISSUE_SUCCESS(HttpStatus.OK, 200, "토큰 재발급에 성공하였습니다."),

    // 뉴스 관련 성공
    NEWS_LIST_SUCCESS(HttpStatus.OK, 200, "분야별 기사 목록 조회 성공입니다."),
    ;

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

}
