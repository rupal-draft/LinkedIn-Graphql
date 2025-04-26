package com.linkedIn.LinkedIn.App.job.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SingleJobApplicationResponse {
    private String message;
    private boolean success;
    private JobApplicationDto jobApplication;
}
