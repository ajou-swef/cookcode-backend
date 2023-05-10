package com.swef.cookcode.recipe.dto.request;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
public class RecipeCreateRequest {

    protected String title;

    protected String description;

    protected List<Long> ingredients;

    protected List<Long> optionalIngredients;

    protected List<StepCreateRequest> steps;

    protected String thumbnail;
}
