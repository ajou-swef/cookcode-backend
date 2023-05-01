package com.swef.cookcode.fridge.dto;

import com.swef.cookcode.fridge.domain.FridgeIngredient;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class FridgeIngredientResponse {
    private Long ingredId;

    private String name;

    private Date expiredAt;

    private String category;

    private String quantity;

    public static FridgeIngredientResponse from(FridgeIngredient fridgeIngred){
        return FridgeIngredientResponse.builder()
                .ingredId(fridgeIngred.getId())
                .name(fridgeIngred.getIngred().getName())
                .expiredAt(fridgeIngred.getExpiredAt())
                .category(fridgeIngred.getIngred().getCategory())
                .quantity(fridgeIngred.getQuantity())
                .build();
    }
}
