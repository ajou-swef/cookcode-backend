package com.swef.cookcode.cookie.dto;

import java.time.LocalDateTime;

public interface CookieDto {
    Long getCookieId();

    String getTitle();

    String getDescription();

    String getVideoUrl();

    Long getUserId();

    String getProfileImage();

    String getNickname();

    Long getRecipeId();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();
}
