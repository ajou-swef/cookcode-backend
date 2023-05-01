package com.swef.cookcode.recipe.dto.response;

import com.swef.cookcode.fridge.dto.response.IngredSimpleResponse;
import com.swef.cookcode.user.dto.response.UserSimpleResponse;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
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

    private Long likeCount;

    private Long commentCount;

    private String thumbnail;
}
