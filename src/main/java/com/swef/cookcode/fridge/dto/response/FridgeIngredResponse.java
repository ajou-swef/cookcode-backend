package com.swef.cookcode.fridge.dto.response;

import com.swef.cookcode.fridge.domain.FridgeIngredient;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class FridgeIngredResponse {
    private final Long ingredId;

    private final String name;

    private final Date expiredAt;

    private final String category;

    private final String quantity;

    public static FridgeIngredResponse from(FridgeIngredient fridgeIngred){
        return FridgeIngredResponse.builder()
                .ingredId(fridgeIngred.getId())
                .name(fridgeIngred.getIngred().getName())
                .expiredAt(fridgeIngred.getExpiredAt())
                .category(fridgeIngred.getIngred().getCategory())
                .quantity(fridgeIngred.getQuantity())
                .build();
    }
}
