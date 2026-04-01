package com.blooddonation.dto.response;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

    private LocalDateTime timestamp = LocalDateTime.now();

    public static <T> ApiResponse<T> ok(String msg, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(msg)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(String msg) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(msg)
                .build();
    }
}