package com.swef.cookcode.fridge.dto;

import com.swef.cookcode.fridge.domain.Ingredient;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IngredientSimpleResponse {
    private Long ingredientId;

    private String name;

    public static IngredientSimpleResponse from(Ingredient ingredient) {
        return IngredientSimpleResponse.builder()
                .ingredientId(ingredient.getId())
                .name(ingredient.getName())
                .build();
    }
}
