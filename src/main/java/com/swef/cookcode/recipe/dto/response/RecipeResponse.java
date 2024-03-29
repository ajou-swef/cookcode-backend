package com.swef.cookcode.recipe.dto.response;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.swef.cookcode.recipe.domain.Recipe;
import com.swef.cookcode.user.dto.response.UserSimpleResponse;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@JsonInclude(NON_NULL)
@AllArgsConstructor
public class RecipeResponse {
    private Long recipeId;

    private UserSimpleResponse user;

    private String title;

    private String description;
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Boolean isLiked;
    private Boolean isCookable;

    private Long likeCount;

    private Long commentCount;

    private String thumbnail;

    private Boolean isPremium;

    private Boolean isAccessible;

    public RecipeResponse(Recipe recipe, Boolean isCookable, Long likeCount, Boolean isLiked, Long commentCount, Boolean isAccessible) {
        this.recipeId = recipe.getId();
        this.user = UserSimpleResponse.from(recipe.getAuthor());
        this.title = recipe.getTitle();
        this.description = recipe.getDescription();
        this.createdAt = recipe.getCreatedAt();
        this.updatedAt = recipe.getUpdatedAt();
        this.isCookable = isCookable;
        this.thumbnail = recipe.getThumbnail();
        this.likeCount = likeCount;
        this.isLiked = isLiked;
        this.commentCount = commentCount;
        this.isPremium = recipe.getIsPremium();
        this.isAccessible = isAccessible;
    }

}
