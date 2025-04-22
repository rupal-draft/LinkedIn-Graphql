package com.linkedIn.LinkedIn.App.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatedPostResponse {

    private String message;
    private boolean success;
    private PostResponse post;
}
