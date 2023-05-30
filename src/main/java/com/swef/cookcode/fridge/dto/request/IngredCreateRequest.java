package com.swef.cookcode.fridge.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class IngredCreateRequest {
    private Long ingredId;

    private LocalDate expiredAt;

    private Long quantity;
}
