package com.example.newquiz.common.base.status;

import org.springframework.http.HttpStatus;

public interface BaseErrorStatus {
    HttpStatus getHttpStatus();
    Integer getCode();
    String getMessage();
}
