package com.backend.devflow.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@RestControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static Log logger = LogFactory.getLog(ControllerAdvisor.class);

    public ControllerAdvisor() {}

    public static ExceptionMessage errorMessage(HttpStatus httpStatus, String error, String path) {
        return ExceptionMessage.builder()
            .timeStamp(LocalDateTime.now().format(dateTimeFormatter))
            .status(httpStatus.value())
            .error(httpStatus.getReasonPhrase())
            .message(error).path(path)
            .build();
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Object> handleBaseException(BaseException ex, HttpServletRequest request) {
        return responseError(
            ex.getStatus(),
            ex.getMessage(),
            request.getRequestURI(),
            ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex, HttpServletRequest request) {
        return responseError(
            HttpStatus.INTERNAL_SERVER_ERROR,
            ex.getMessage(),
            request.getRequestURI(),
            ex);
    }

    private ResponseEntity<Object> responseError(HttpStatus status, String error, String path, Throwable ex) {
        logger.error("Error: " + error, ex);
        ExceptionMessage exceptionMessage = errorMessage(status, error, path);
        return new ResponseEntity<>(exceptionMessage, status);
    }
}
