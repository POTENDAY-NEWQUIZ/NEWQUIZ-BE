package com.example.newquiz.common.status;

import com.example.newquiz.common.base.status.BaseErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorStatus {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, 400, "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 401, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, 403, "접근 권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, 404, "요청한 자원을 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, 405, "허용되지 않은 메소드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, "서버 내부 오류입니다."),

    // 토큰 관련 에러
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, 401, "유효하지 않은 토큰입니다."),

    // 회원 관련 에러
    NOT_FOUND_USER_BY_USERNAME(HttpStatus.NOT_FOUND, 404, "해당하는 닉네임의 사용자를 찾을 수 없습니다."),
    NOT_FOUND_USER_BY_USER_ID(HttpStatus.NOT_FOUND, 404, "해당하는 사용자를 찾을 수 없습니다."),
    ALREADY_REGISTERED_USER(HttpStatus.BAD_REQUEST, 400, "이미 가입된 사용자입니다."),

    // AI 관련 에러
    AI_CLIENT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, "AI 서버와의 통신 중 클라이언트 오류가 발생했습니다."),
    AI_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, "AI 서버와의 통신 중 서버 오류가 발생했습니다."),
    INVALID_AI_RESPONSE(HttpStatus.INTERNAL_SERVER_ERROR, 500, "AI 서버로부터 올바르지 않은 응답을 받았습니다."),

    // 퀴즈 관련 에러
    NOT_FOUND_QUIZ(HttpStatus.NOT_FOUND, 404, "해당하는 뉴스에 대한 퀴즈가 없습니다."),
    ALREADY_COMPLETED_QUIZ(HttpStatus.BAD_REQUEST, 400, "이미 퀴즈를 완료한 뉴스입니다."),
    ;

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;
}
