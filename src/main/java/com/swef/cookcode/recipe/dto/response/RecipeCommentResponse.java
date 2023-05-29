package com.swef.cookcode.recipe.dto.response;

import com.swef.cookcode.user.dto.response.UserSimpleResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
public class RecipeCommentResponse {
    private final Long commentId;

    private final Long recipeId;

    private final UserSimpleResponse user;

    private final String comment;
}
