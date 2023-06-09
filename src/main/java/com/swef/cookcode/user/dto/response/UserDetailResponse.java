package com.swef.cookcode.user.dto.response;

import static org.springframework.util.StringUtils.hasText;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.swef.cookcode.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserDetailResponse {
    private final Long userId;

    private final String email;

    private final String nickname;

    private final String profileImage;

    private final String status;

    private final String authority;

    private final Boolean isSubscribed;

    private final Long subscriberCount;

    public UserDetailResponse(User user, Boolean isSubscribed, Long subscriberCount) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.profileImage = hasText(user.getProfileImage()) ? user.getProfileImage() : null;
        this.status = user.getStatus().toString();
        this.authority = user.getAuthority().toString();
        this.isSubscribed = isSubscribed;
        this.subscriberCount = subscriberCount;
    }

    public static UserDetailResponse from(User user) {
        return UserDetailResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImage(hasText(user.getProfileImage()) ? user.getProfileImage() : null)
                .status(user.getStatus().toString())
                .authority(user.getAuthority().toString())
                .build();
    }
}
