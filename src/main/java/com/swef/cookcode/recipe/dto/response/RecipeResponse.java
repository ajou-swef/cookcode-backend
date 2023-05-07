package com.swef.cookcode.recipe.dto.response;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.swef.cookcode.fridge.dto.IngredientSimpleResponse;
import com.swef.cookcode.recipe.domain.Recipe;
import com.swef.cookcode.recipe.domain.RecipeIngred;
import com.swef.cookcode.user.dto.response.UserSimpleResponse;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(NON_NULL)
public class RecipeResponse {
    private Long recipeId;

    private UserSimpleResponse user;

    private String title;

    private String description;

    private List<IngredientSimpleResponse> ingredients;

    private List<IngredientSimpleResponse> optionalIngredients;

    private List<StepResponse> steps;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Boolean isLiked;

    private Long likeCount;

    private Long commentCount;

    private String thumbnail;

    public static RecipeResponse from(Recipe recipe) {
        return RecipeResponse.builder()
                .recipeId(recipe.getId())
                .user(UserSimpleResponse.from(recipe.getAuthor()))
                .title(recipe.getTitle())
                .description(recipe.getDescription())
                .ingredients(recipe.getIngredients().stream().map(i -> IngredientSimpleResponse.from(i.getIngredient())).toList())
                .optionalIngredients(recipe.getOptionalIngredients().stream().map(i -> IngredientSimpleResponse.from(i.getIngredient())).toList())
                .steps(recipe.getSteps().stream().map(s -> StepResponse.from(s, s.getPhotos(), s.getVideos())).toList())
                .createdAt(recipe.getCreatedAt())
                .updatedAt(recipe.getUpdatedAt())
                .thumbnail(recipe.getThumbnail())
                .build();
    }

    public static RecipeResponse getMeta(Recipe recipe) {
        return RecipeResponse.builder()
                .recipeId(recipe.getId())
                .user(UserSimpleResponse.from(recipe.getAuthor()))
                .title(recipe.getTitle())
                .description(recipe.getDescription())
                .ingredients(recipe.getIngredients().stream().filter(RecipeIngred::getIsNecessary).map(i -> IngredientSimpleResponse.from(i.getIngredient())).toList())
                .optionalIngredients(recipe.getOptionalIngredients().stream().filter(i -> !i.getIsNecessary()).map(i -> IngredientSimpleResponse.from(i.getIngredient())).toList())
                .createdAt(recipe.getCreatedAt())
                .updatedAt(recipe.getUpdatedAt())
                .thumbnail(recipe.getThumbnail())
                .build();
    }
}
