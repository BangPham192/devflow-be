package com.backend.devflow.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class BaseException extends RuntimeException{
    public final HttpStatus status;
    public final String translationKey;
    public final String[] params;
    public final Throwable cause;

    public BaseException(HttpStatus status, String translationKey, Throwable cause, String... params) {
        this.status = status;
        this.translationKey = translationKey;
        this.params = params;
        this.cause = cause;
    }

    @Override
    public String getMessage() {
        return translationKey;
    }
}
