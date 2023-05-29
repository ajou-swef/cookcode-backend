package com.swef.cookcode.recipe.dto.request;

import jakarta.validation.constraints.NotBlank;

public class RecipeCommentCreateRequest {
    @NotBlank
    private String comment;
}
