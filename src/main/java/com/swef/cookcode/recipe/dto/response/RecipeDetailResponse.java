package com.swef.cookcode.recipe.dto.response;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.swef.cookcode.fridge.dto.response.IngredSimpleResponse;
import com.swef.cookcode.recipe.domain.Recipe;
import com.swef.cookcode.recipe.domain.RecipeIngred;
import com.swef.cookcode.recipe.dto.projection.IngredientProjection;
import java.util.List;
import lombok.Getter;

@Getter
@JsonInclude(NON_NULL)
public class RecipeDetailResponse extends RecipeResponse{

    private final List<StepResponse> steps;

    private List<IngredSimpleResponse> ingredients;

    private List<IngredSimpleResponse> optionalIngredients;


    public RecipeDetailResponse(Recipe recipe, Boolean isCookable, Long likeCount, Boolean isLiked, Long commentCount, Boolean isAccessible) {
        super(recipe, isCookable, likeCount, isLiked, commentCount, isAccessible);
        this.steps = recipe.getSteps().stream().map(s -> StepResponse.from(s, s.getPhotos(), s.getVideos())).toList();
    }

    public void setIngredients(List<IngredientProjection> projections) {
        this.ingredients = projections.stream().filter(IngredientProjection::getIsNecessary).map(
                IngredSimpleResponse::from).toList();
        this.optionalIngredients = projections.stream().filter(p -> !p.getIsNecessary()).map(
                IngredSimpleResponse::from).toList();
    }
}
