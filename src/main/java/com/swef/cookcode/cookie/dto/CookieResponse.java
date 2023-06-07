package com.swef.cookcode.cookie.dto;

import com.swef.cookcode.cookie.domain.Cookie;
import com.swef.cookcode.user.dto.response.UserSimpleResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import static java.util.Objects.isNull;

@Getter
public class CookieResponse {

    private final Long cookieId;

    private final String title;

    private final String desc;

    private final String thumbnailUrl;

    private final String videoUrl;

    private final Long recipeId;

    private final LocalDateTime createdAt;

    private final UserSimpleResponse user;

    private final Long isLiked;

    private final Long likeCount;

    private final Long commentCount;

    public CookieResponse(Cookie cookie, Long isLiked, Long likeCount, Long commentCount) {
        this.cookieId = cookie.getId();
        this.title = cookie.getTitle();
        this.desc = cookie.getDescription();
        this.thumbnailUrl = cookie.getThumbnailUrl();
        this.videoUrl = cookie.getVideoUrl();
        this.recipeId = isNull(cookie.getRecipe())? null : cookie.getRecipe().getId();
        this.createdAt = cookie.getCreatedAt();
        this.user = UserSimpleResponse.from(cookie.getUser());
        this.isLiked = isLiked;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }
}
