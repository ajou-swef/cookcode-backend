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

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Boolean isLiked;
    private Boolean isCookable;

    private Long likeCount;

    private Long commentCount;

    private String thumbnail;

    public RecipeResponse(Recipe recipe, Boolean isCookable, Long likeCount, Boolean isLiked, Long commentCount) {
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
        this.likeCount = likeCount;
        this.isLiked = isLiked;
        this.commentCount = commentCount;
    }

    private static List<IngredSimpleResponse> convert(List<RecipeIngred> ingreds) {
        return ingreds.stream().map(i -> IngredSimpleResponse.from(i.getIngredient())).toList();
    }
}
