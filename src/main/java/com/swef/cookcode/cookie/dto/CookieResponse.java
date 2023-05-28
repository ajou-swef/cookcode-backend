package com.swef.cookcode.cookie.dto;

import com.swef.cookcode.cookie.domain.Cookie;
import com.swef.cookcode.user.domain.User;
import com.swef.cookcode.user.dto.response.UserSimpleResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class CookieResponse {

    private Long id;

    private String title;

    private String desc;

    private String videoUrl;

    private LocalDateTime createdAt;

    private UserSimpleResponse user;

    private Long isLiked;

    private Long likeCount;

    private Long commentCount;

    public CookieResponse(Cookie cookie, User user, Long isLiked, Long likeCount, Long commentCount) {
        this.id = cookie.getId();
        this.title = cookie.getTitle();
        this.desc = cookie.getDescription();
        this.videoUrl = cookie.getVideoUrl();
        this.createdAt = cookie.getCreatedAt();
        this.user = UserSimpleResponse.from(user);
        this.isLiked = isLiked;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }

}
