package com.linkedIn.LinkedIn.App.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MultiplePostsResponse {
    private String message;
    private boolean success;
    private int totalPosts;
    private List<DetailedPostResponse> posts;
}
