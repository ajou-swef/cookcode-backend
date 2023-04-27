package com.swef.cookcode.recipe.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VideoResponse {
    private Long stepVideoId;

    private String videoUrl;
}
