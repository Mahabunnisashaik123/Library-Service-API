package com.example.libraryService.response;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiCode {
    SUCCESS(200, "Request processed successfully"),
    CREATED(201, "Resource created successfully"),
    UPDATED(200, "Resource updated successfully"),
    DELETED(200, "Resource deleted successfully"),
    VALIDATION_ERROR(400, "Validation failed"),
    NOT_FOUND(404, "Resource not found"),
    INTERNAL_ERROR(500, "Internal server error"),
    SERVICE_UNAVAILABLE(503, "Service temporarily unavailable");

    private final int statusCode;
    private final String message;
}
