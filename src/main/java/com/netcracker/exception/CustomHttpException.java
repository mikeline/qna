package com.netcracker.exception;

import org.springframework.http.HttpStatus;

public class CustomHttpException extends QnAException {

    private static final long serialVersionUID = 1L;

    private final String message;
    private final HttpStatus httpStatus;
    private final String stackTraceString;

    public CustomHttpException(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.stackTraceString = "Not Set";
    }

    public CustomHttpException(String message, HttpStatus httpStatus, String stackTraceString) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.stackTraceString = stackTraceString;
    }

    @Override
    public String getMessage() {
        return stackTraceString;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getStackTraceString() {return stackTraceString; }

}