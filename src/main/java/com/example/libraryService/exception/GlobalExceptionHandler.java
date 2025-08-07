package com.example.libraryService.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.libraryService.response.ApiCode;
import com.example.libraryService.response.ApiResponse;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ApiResponse<Object> handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ApiResponse.builder()
                .status(ApiCode.NOT_FOUND.getStatusCode())
                .message(ex.getMessage())
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(BadRequestException.class)
    public ApiResponse<Object> handleBadRequest(BadRequestException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return ApiResponse.builder()
                .status(ApiCode.VALIDATION_ERROR.getStatusCode())
                .message(ex.getMessage())
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Object> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errorMap = ex.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField(),
                        fieldError -> fieldError.getDefaultMessage(),
                        (existing, replacement) -> existing, 
                        LinkedHashMap::new
                ));

        log.debug("Validation failed: {}", errorMap);

        return ApiResponse.builder()
                .status(ApiCode.VALIDATION_ERROR.getStatusCode())
                .message("Validation failed")
                .data(errorMap)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResponse<Object> handleConstraint(ConstraintViolationException ex) {
        log.debug("Constraint violation: {}", ex.getMessage());
        return ApiResponse.builder()
                .status(ApiCode.VALIDATION_ERROR.getStatusCode())
                .message("Invalid input: " + ex.getMessage())
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse<Object> handleUnreadable(HttpMessageNotReadableException ex) {
        log.debug("Malformed JSON: {}", ex.getMessage());
        return ApiResponse.builder()
                .status(ApiCode.VALIDATION_ERROR.getStatusCode())
                .message("Invalid request format")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Object> handleAll(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return ApiResponse.builder()
                .status(ApiCode.INTERNAL_ERROR.getStatusCode())
                .message("Something went wrong")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
}
