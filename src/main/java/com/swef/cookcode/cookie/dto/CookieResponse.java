package com.swef.cookcode.cookie.dto;

import com.swef.cookcode.cookie.domain.Cookie;
import com.swef.cookcode.user.domain.User;
import com.swef.cookcode.user.dto.response.UserSimpleResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CookieResponse {

    private final Long id;

    private final UserSimpleResponse user;

    private final String title;

    private final String desc;

    private final String videoUrl;

    public CookieResponse(Cookie cookie) {
        this.id = cookie.getId();
        this.title = cookie.getTitle();
        this.desc = cookie.getDescription();
        this.videoUrl = cookie.getVideoUrl();
        this.user = UserSimpleResponse.from(cookie.getUser());
    }

    public static CookieResponse of(Cookie cookie, User user){
        return new CookieResponse(cookie);
    }
}
