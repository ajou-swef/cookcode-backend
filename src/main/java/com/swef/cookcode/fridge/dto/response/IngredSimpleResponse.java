package com.swef.cookcode.fridge.dto.response;

import com.swef.cookcode.fridge.domain.Ingredient;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IngredSimpleResponse {
    private final Long ingredientId;

    private final String name;

    public static IngredSimpleResponse from(Ingredient ingredient) {
        return IngredSimpleResponse.builder()
                .ingredientId(ingredient.getId())
                .name(ingredient.getName())
                .build();
    }
}
