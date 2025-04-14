package com.linkedIn.LinkedIn.App.common.advices;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;


public class ApiResponse<T>{

    private boolean success;

    private String message;

    private T data;

    private ApiError error;

    @JsonFormat(pattern = "hh-mm-ss dd-MM-yyyy")
    private LocalDateTime timestamp;

    public ApiResponse(boolean success, String message, T data, ApiError error, LocalDateTime timestamp) {

        this.success = success;
        this.message = message;
        this.data = data;
        this.error = error;
        this.timestamp = timestamp;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> error(String message, T data, ApiError error) {
        return new ApiResponse<>(false, message, null, error, LocalDateTime.now());
    }
}
