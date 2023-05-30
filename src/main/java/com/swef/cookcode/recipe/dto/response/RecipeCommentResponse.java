package com.swef.cookcode.recipe.dto.response;

import com.swef.cookcode.recipe.domain.RecipeComment;
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

    public static RecipeCommentResponse from(RecipeComment comment) {
        return RecipeCommentResponse.builder()
                .commentId(comment.getId())
                .recipeId(comment.getRecipe().getId())
                .user(UserSimpleResponse.from(comment.getUser()))
                .comment(comment.getComment())
                .build();
    }
}
