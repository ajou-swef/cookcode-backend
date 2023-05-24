package com.swef.cookcode.cookie.dto;

import com.swef.cookcode.cookie.domain.Cookie;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CookieResponse {

    private final Long id;

    private final String title;

    private final String desc;

    private final String videoUrl;

    public static CookieResponse of(Cookie cookie){
        return new CookieResponse(cookie.getId(), cookie.getTitle(), cookie.getDescription(), cookie.getVideoUrl());
    }
}
