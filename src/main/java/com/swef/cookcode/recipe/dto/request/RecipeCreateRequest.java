package com.swef.cookcode.recipe.dto.request;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class RecipeCreateRequest {

    private String title;

    private String description;

    private List<Long> ingredients;

    private List<Long> optionalIngredients;

    private List<StepCreateRequest> steps;

    private String thumbnail;

    private List<String> deletedThumbnails;
}
