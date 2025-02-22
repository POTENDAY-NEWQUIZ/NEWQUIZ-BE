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
    NO_CONTENT(HttpStatus.NO_CONTENT, 204, "성공적으로 삭제되었습니다.");

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

}
