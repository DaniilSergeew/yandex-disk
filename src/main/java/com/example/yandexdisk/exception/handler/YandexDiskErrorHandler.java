package com.example.yandexdisk.exception.handler;

import com.example.yandexdisk.exception.EntityNotFoundException;
import com.example.yandexdisk.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@Slf4j
public class YandexDiskErrorHandler {
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Void> handleValidationException(ValidationException ex) {
        //log.error("ValidationException " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error("EntityNotFoundException " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Void> handlerMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        log.error("MethodArgumentNotValidException " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

}
