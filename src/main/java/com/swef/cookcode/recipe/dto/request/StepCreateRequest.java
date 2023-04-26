package com.swef.cookcode.recipe.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class StepCreateRequest {

    private Long seq;

    private String title;

    private String description;

    private String[] videos;

    private String[] photos;

    private String[] deletedVideos;

    private String[] deletedPhotos;
}
