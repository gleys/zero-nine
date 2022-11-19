package com.example.zeronine.utils;

import lombok.Data;
import org.springframework.http.HttpStatus;

public class ResponseForm {

    public static<T> Result<T> success(T response) {
        return new Result<>(true, response, null);
    }

    public static Result error(Throwable throwable, HttpStatus httpStatus) {
        return new Result(false, null, new ResponseError(throwable.getMessage(), httpStatus.value()));
    }

    public static<T> Result<T> error(String message, HttpStatus httpStatus) {
        return new Result<>(false, null, new ResponseError(message, httpStatus.value()));
    }

    @Data
    public static class ResponseError {
        private final String message;
        private final int status;
    }

    @Data
    public static class Result<T> {
        private final boolean success;
        private final T response;
        private final ResponseError error;
    }
}
