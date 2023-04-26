package com.swef.cookcode.recipe.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PhotoResponse {
    private Long stepPhotoId;

    private String photoUrl;
}
