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
    GET_HOME_INFO_SUCCESS(HttpStatus.OK, 200, "홈 화면 정보 조회 성공입니다."),

    // 토큰 관련 성공
    TOKEN_REISSUE_SUCCESS(HttpStatus.OK, 200, "토큰 재발급에 성공하였습니다."),

    // 뉴스 관련 성공
    NEWS_LIST_SUCCESS(HttpStatus.OK, 200, "분야별 기사 목록 조회 성공입니다."),
    NEWS_DETAIL_SUCCESS(HttpStatus.OK, 200, "기사 원문 조회 성공입니다."),
    SAVE_SUMMARY_SUCCESS(HttpStatus.CREATED, 201, "요약 제출 및 AI 피드백 받기 성공입니다."),
    SEND_LEVEL_FEEDBACK_SUCCESS(HttpStatus.CREATED, 201, "AI 난이도 조사 제출이 성공되었습니다."),

    // 퀴즈 관련 성공
    GET_QUIZ_INFO_SUCCESS(HttpStatus.OK, 200, "퀴즈를 성공적으로 조회했습니다."),
    SAVE_QUIZ_RESULT_SUCCESS(HttpStatus.CREATED, 201, "퀴즈 정답 제출 성공입니다."),
    ;

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

}
