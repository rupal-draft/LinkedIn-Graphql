package com.linkedIn.LinkedIn.App.common.advices;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class ApiError {
    private LocalDateTime timestamp;
    private HttpStatus status;
    private String message;
    private String path;

}
