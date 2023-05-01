package com.swef.cookcode.fridge.dto.response;

import com.swef.cookcode.fridge.domain.Ingredient;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IngredSimpleResponse {
    private Long ingredientId;

    private String name;

    public static IngredSimpleResponse from(Ingredient ingredient) {
        return IngredSimpleResponse.builder()
                .ingredientId(ingredient.getId())
                .name(ingredient.getName())
                .build();
    }
}
