package com.example.newquiz.common.base.status;

import org.springframework.http.HttpStatus;

public interface BaseSuccessStatus {
    HttpStatus getHttpStatus();
    Integer getCode();
    String getMessage();
}
