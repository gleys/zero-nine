package com.example.zeronine.errors;

import com.example.zeronine.utils.ResponseForm;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GeneralExceptionHandler {

    private ResponseEntity<ResponseForm.Result<?>> response(Throwable throwable, HttpStatus httpStatus) {
        return response(throwable.getMessage(), httpStatus);
    }

    private ResponseEntity<ResponseForm.Result<?>> response(String message, HttpStatus httpStatus) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        return new ResponseEntity<>(ResponseForm.error(message, httpStatus), httpHeaders, httpStatus);
    }

    @ExceptionHandler({
            IllegalStateException.class,
            NotFoundException.class
    })
    public ResponseEntity<?> handleClientException(Exception e) {
        log.info("Invalid client request: {}", e);

        return response(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> handleUnAuthorizedException(Exception e) {
        log.info("Unauthorized User : {}", e);

        return response(e, HttpStatus.UNAUTHORIZED);
    }


}
