package com.swef.cookcode.fridge.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class IngredUpdateRequest {
    private Long ingredId;

    private Date expiredAt;

    private String quantity;
}
