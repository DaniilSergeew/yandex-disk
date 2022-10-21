package com.example.yandexdisk.exception.handler;

import com.example.yandexdisk.exception.EntityNotFoundException;
import com.example.yandexdisk.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class YandexDiskErrorHandlingController {
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Void> handleValidationException(ValidationException ex) {
        log.error("ValidationException " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error("EntityNotFoundException " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    // Todo: добавить обработчик для sql exception
}
