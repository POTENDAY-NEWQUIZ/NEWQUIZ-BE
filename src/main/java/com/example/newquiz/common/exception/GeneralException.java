package com.example.newquiz.common.exception;

import com.example.newquiz.common.base.status.BaseErrorStatus;
import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException{
    protected final BaseErrorStatus errorStatus;

    public GeneralException(BaseErrorStatus errorStatus) {
        super(errorStatus.getMessage());
        this.errorStatus = errorStatus;
    }
}
