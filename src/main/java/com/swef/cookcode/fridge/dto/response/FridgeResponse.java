package com.swef.cookcode.fridge.dto.response;

import com.swef.cookcode.fridge.domain.FridgeIngredient;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class FridgeResponse {

    private List<FridgeIngredResponse> ingreds;

    public static FridgeResponse from(List<FridgeIngredient> ingreds){
        return FridgeResponse.builder()
                .ingreds(ingredFrom(ingreds))
                .build();
    }

    private static List<FridgeIngredResponse> ingredFrom(List<FridgeIngredient> ingreds) {
        return ingreds.stream()
                .map(FridgeIngredResponse::from)
                .toList();
    }
}
