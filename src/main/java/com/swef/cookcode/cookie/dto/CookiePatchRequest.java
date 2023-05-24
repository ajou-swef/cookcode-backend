package com.swef.cookcode.cookie.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CookiePatchRequest {

    private final String title;

    private final String desc;

}
