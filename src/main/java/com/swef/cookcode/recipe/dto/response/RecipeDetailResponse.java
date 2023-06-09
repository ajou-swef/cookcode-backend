package com.swef.cookcode.recipe.dto.response;

import com.swef.cookcode.fridge.dto.response.IngredSimpleResponse;
import com.swef.cookcode.recipe.domain.Recipe;
import com.swef.cookcode.recipe.domain.RecipeIngred;
import java.util.List;
import lombok.Getter;

@Getter
public class RecipeDetailResponse extends RecipeResponse{

    private final List<StepResponse> steps;

    private List<IngredSimpleResponse> ingredients;

    private List<IngredSimpleResponse> optionalIngredients;


    public RecipeDetailResponse(Recipe recipe, Boolean isCookable, Long likeCount, Boolean isLiked, Long commentCount) {
        super(recipe, isCookable, likeCount, isLiked, commentCount);
        this.steps = recipe.getSteps().stream().map(s -> StepResponse.from(s, s.getPhotos(), s.getVideos())).toList();
        this.ingredients = convert(recipe.getNecessaryIngredients());
        this.optionalIngredients = convert(recipe.getOptionalIngredients());
    }

    private static List<IngredSimpleResponse> convert(List<RecipeIngred> ingreds) {
        return ingreds.stream().map(i -> IngredSimpleResponse.from(i.getIngredient())).toList();
    }
}
