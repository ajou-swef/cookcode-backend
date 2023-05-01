package com.swef.cookcode.fridge.dto;

import com.swef.cookcode.fridge.domain.FridgeIngredient;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class FridgeResponse {

    private List<FridgeIngredientResponse> ingreds;

    public static FridgeResponse from(List<FridgeIngredient> ingreds){
        return FridgeResponse.builder()
                .ingreds(ingredFrom(ingreds))
                .build();
    }

    private static List<FridgeIngredientResponse> ingredFrom(List<FridgeIngredient> ingreds) {
        return ingreds.stream()
                .map(FridgeIngredientResponse::from)
                .toList();
    }
}
