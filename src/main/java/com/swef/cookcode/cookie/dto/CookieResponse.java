package com.swef.cookcode.cookie.dto;

import com.swef.cookcode.cookie.domain.Cookie;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CookieResponse {

    private final String title;

    private final String desc;

    private final String videoUrl;

    public static CookieResponse of(Cookie cookie){
        return new CookieResponse(cookie.getTitle(), cookie.getDescription(), cookie.getVideoUrl());
    }
}
