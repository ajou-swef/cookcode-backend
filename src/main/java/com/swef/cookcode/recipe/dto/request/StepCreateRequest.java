package com.swef.cookcode.recipe.dto.request;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class StepCreateRequest {

    private Long seq;

    private String title;

    private String description;

    private List<String> videos;

    private List<String> photos;

    private List<String> deletedVideos;

    private List<String> deletedPhotos;
}
