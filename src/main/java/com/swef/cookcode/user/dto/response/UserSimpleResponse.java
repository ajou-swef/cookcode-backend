package com.swef.cookcode.user.dto.response;

import static org.springframework.util.StringUtils.hasText;

import com.swef.cookcode.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserSimpleResponse {
    private Long userId;

    private String profileImage;

    private String nickname;

    private Boolean isQuit;

    public static UserSimpleResponse from(User user) {
        return UserSimpleResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileImage(hasText(user.getProfileImage()) ? user.getProfileImage() : null)
                .isQuit(user.getIsQuit())
                .build();
    }
}
