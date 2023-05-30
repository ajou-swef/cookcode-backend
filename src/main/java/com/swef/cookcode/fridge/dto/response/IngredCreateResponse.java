package com.swef.cookcode.fridge.dto.response;

import com.swef.cookcode.fridge.domain.Category;
import com.swef.cookcode.fridge.domain.FridgeIngredient;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class IngredCreateResponse {

    private final Long fridgeIngredId;

    private final Long fridgeId;

    private final Long ingredId;

    private final String name;

    private final LocalDate expiredAt;

    private final Category category;

    private final Long quantity;

    public static IngredCreateResponse from(FridgeIngredient fridgeIngred){
        return IngredCreateResponse.builder()
                .fridgeIngredId(fridgeIngred.getId())
                .fridgeId(fridgeIngred.getFridge().getId())
                .ingredId(fridgeIngred.getIngred().getId())
                .name(fridgeIngred.getIngred().getName())
                .expiredAt(fridgeIngred.getExpiredAt())
                .category(fridgeIngred.getIngred().getCategory())
                .quantity(fridgeIngred.getQuantity())
                .build();
    }
}
