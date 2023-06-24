package com.swef.cookcode.recipe.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class StepCreateRequest {

    @NotNull
    protected Long seq;
    
    protected String title;

    @NotBlank
    protected String description;

    protected List<String> videos;

    protected List<String> photos;

    protected List<String> deletedVideos;

    protected List<String> deletedPhotos;
}
