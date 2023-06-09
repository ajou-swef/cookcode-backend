package com.swef.cookcode.fridge.dto.response;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.swef.cookcode.fridge.domain.Ingredient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@JsonInclude(NON_NULL)
public class IngredSimpleResponse {
    private final Long ingredientId;

    private final String name;

    private final Boolean isLack;

    private final String ingredThumbnail;

    public static IngredSimpleResponse from(Ingredient ingredient) {
        return IngredSimpleResponse.builder()
                .ingredientId(ingredient.getId())
                .name(ingredient.getName())
                .build();
    }
}
