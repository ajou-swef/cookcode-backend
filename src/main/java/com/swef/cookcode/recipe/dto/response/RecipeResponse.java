package com.swef.cookcode.recipe.dto.response;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.swef.cookcode.fridge.dto.response.IngredSimpleResponse;
import com.swef.cookcode.recipe.domain.Recipe;
import com.swef.cookcode.recipe.domain.RecipeIngred;
import com.swef.cookcode.user.domain.User;
import com.swef.cookcode.user.dto.response.UserSimpleResponse;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(NON_NULL)
@AllArgsConstructor
public class RecipeResponse {
    private Long recipeId;

    private UserSimpleResponse user;

    private String title;

    private String description;

    private List<IngredSimpleResponse> ingredients;

    private List<IngredSimpleResponse> optionalIngredients;

    private List<StepResponse> steps;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Boolean isLiked;
    private Boolean isCookable;

    private Long likeCount;

    private Long commentCount;

    private String thumbnail;

    public RecipeResponse(Recipe recipe, Boolean isCookable) {
        this.recipeId = recipe.getId();
        this.user = UserSimpleResponse.from(recipe.getAuthor());
        this.title = recipe.getTitle();
        this.description = recipe.getDescription();
        this.ingredients = convert(recipe.getNecessaryIngredients());
        this.optionalIngredients = convert(recipe.getOptionalIngredients());
        this.createdAt = recipe.getCreatedAt();
        this.updatedAt = recipe.getUpdatedAt();
        this.isCookable = isCookable;
        this.thumbnail = recipe.getThumbnail();
    }

    public static RecipeResponse from(Recipe recipe) {
        return RecipeResponse.builder()
                .recipeId(recipe.getId())
                .user(UserSimpleResponse.from(recipe.getAuthor()))
                .title(recipe.getTitle())
                .description(recipe.getDescription())
                .ingredients(convert(recipe.getNecessaryIngredients()))
                .optionalIngredients(convert(recipe.getOptionalIngredients()))
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
                .ingredients(convert(recipe.getNecessaryIngredients()))
                .optionalIngredients(convert(recipe.getOptionalIngredients()))
                .createdAt(recipe.getCreatedAt())
                .updatedAt(recipe.getUpdatedAt())
                .thumbnail(recipe.getThumbnail())
                .build();
    }

    private static List<IngredSimpleResponse> convert(List<RecipeIngred> ingreds) {
        return ingreds.stream().map(i -> IngredSimpleResponse.from(i.getIngredient())).toList();
    }
}
