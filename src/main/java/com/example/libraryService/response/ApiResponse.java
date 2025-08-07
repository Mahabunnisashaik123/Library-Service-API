package com.example.libraryService.response;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> of(ApiCode code, T data) {
        return ApiResponse.<T>builder()
                .status(code.getStatusCode())
                .message(code.getMessage())
                .data(data)
                .timestamp(LocalDateTime.now())  
                .build();
    }
}
