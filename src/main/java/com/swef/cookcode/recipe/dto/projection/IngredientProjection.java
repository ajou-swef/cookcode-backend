package com.swef.cookcode.recipe.dto.projection;

import com.swef.cookcode.fridge.domain.Ingredient;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IngredientProjection {

    private final Ingredient ingredient;

    private final Boolean isLack;

    private final Boolean isNecessary;
}
