package com.swef.cookcode.fridge.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class IngredUpdateRequest {
    private Long ingredId;

    private LocalDate expiredAt;

    private int quantity;
}
