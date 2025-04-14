package com.linkedIn.LinkedIn.App.common.advices;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T>{

    private boolean success;

    private String message;

    private T data;

    private ApiError error;

    @JsonFormat(pattern = "hh-mm-ss dd-MM-yyyy")
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> error(String message, T data, ApiError error) {
        return new ApiResponse<>(false, message, null, error, LocalDateTime.now());
    }
}
