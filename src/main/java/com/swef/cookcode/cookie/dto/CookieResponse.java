package com.swef.cookcode.cookie.dto;

import static java.util.Objects.nonNull;

import com.swef.cookcode.cookie.domain.Cookie;
import com.swef.cookcode.user.domain.User;
import com.swef.cookcode.user.dto.response.UserSimpleResponse;
import java.time.LocalDateTime;
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

    private final Long recipeId;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;

    public CookieResponse(Cookie cookie) {
        this.id = cookie.getId();
        this.title = cookie.getTitle();
        this.desc = cookie.getDescription();
        this.videoUrl = cookie.getVideoUrl();
        this.user = UserSimpleResponse.from(cookie.getUser());
        this.createdAt = cookie.getCreatedAt();
        this.updatedAt = cookie.getUpdatedAt();
        this.recipeId = nonNull(cookie.getRecipe()) ? cookie.getRecipe().getId() : null;
    }

    public static CookieResponse of(Cookie cookie){
        return new CookieResponse(cookie);
    }

    public static CookieResponse of(CookieDto dto) {
        return CookieResponse.builder()
                .id(dto.getCookieId())
                .title(dto.getTitle())
                .desc(dto.getDescription())
                .videoUrl(dto.getVideoUrl())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .recipeId(dto.getRecipeId())
                .user(UserSimpleResponse.builder()
                        .userId(dto.getUserId())
                        .nickname(dto.getNickname())
                        .profileImage(dto.getProfileImage()).build())
                .build();
    }
}
