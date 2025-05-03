package com.linkedIn.LinkedIn.App.message.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SingleMessageResponse {
    private String message;
    private boolean success;
    private MessageDto messageDto;
}
