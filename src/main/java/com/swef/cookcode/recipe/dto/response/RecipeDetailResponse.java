package com.swef.cookcode.recipe.dto.response;

import com.swef.cookcode.recipe.domain.Recipe;
import java.util.List;
import lombok.Getter;

@Getter
public class RecipeDetailResponse extends RecipeResponse{

    private final List<StepResponse> steps;

    public RecipeDetailResponse(Recipe recipe, Boolean isCookable, Long likeCount, Boolean isLiked, Long commentCount) {
        super(recipe, isCookable, likeCount, isLiked, commentCount);
        this.steps = recipe.getSteps().stream().map(s -> StepResponse.from(s, s.getPhotos(), s.getVideos())).toList();
    }
}
