package com.netcracker.exception;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String message;
    private final HttpStatus httpStatus;
    private final String stackTraceString;

    public CustomException(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.stackTraceString = "Not Set";
    }

    public CustomException(String message, HttpStatus httpStatus, String stackTraceString) {
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