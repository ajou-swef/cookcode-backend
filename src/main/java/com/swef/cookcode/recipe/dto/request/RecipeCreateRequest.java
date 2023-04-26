package com.swef.cookcode.recipe.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class RecipeCreateRequest {

    private String title;

    private String description;

    private Long[] ingredients;

    private Long[] optionalIngredients;

    private StepCreateRequest[] steps;
}
