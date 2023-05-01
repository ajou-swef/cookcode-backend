package com.swef.cookcode.fridge.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
public class IngredCreateRequest {
    private Long ingredId;

    private Date expiredAt;

    private String quantity;
}
