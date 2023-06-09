package com.swef.cookcode.fridge.dto.response;

import com.swef.cookcode.fridge.domain.Ingredient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class IngredSimpleResponse {
    private final Long ingredientId;

    private final String name;

    private final Boolean isLack;

    public static IngredSimpleResponse from(Ingredient ingredient) {
        return IngredSimpleResponse.builder()
                .ingredientId(ingredient.getId())
                .name(ingredient.getName())
                .build();
    }
}
