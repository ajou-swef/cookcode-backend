package com.swef.cookcode.recipe.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RecipeCommentCreateRequest {
    @NotBlank
    private String comment;
}
