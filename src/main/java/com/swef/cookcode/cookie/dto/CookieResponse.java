package com.swef.cookcode.cookie.dto;

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

}
