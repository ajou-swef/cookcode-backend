package com.swef.cookcode.recipe.dto.request;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class StepCreateRequest {

    protected Long seq;

    protected String title;

    protected String description;

    protected List<String> videos;

    protected List<String> photos;

    protected List<String> deletedVideos;

    protected List<String> deletedPhotos;
}
