package com.linkedIn.LinkedIn.App.job.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JobResponseDto {
    private String message;
    private boolean success;
    private JobDto job;
}
