package ru.practicum.stats.statsservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST) //400
    public ErrorResponse handleBadRequestException(final BadRequestException e) {
        log.info("400: {}", e.getMessage());
        String reason = "BAD_REQUEST";
        return getErrorResponse(HttpStatus.BAD_REQUEST, reason, e.getMessage());
    }


    private ErrorResponse getErrorResponse(HttpStatus status, String reason, String message) {
        return ErrorResponse.builder()
                .status(status.name())
                .reason(reason)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
