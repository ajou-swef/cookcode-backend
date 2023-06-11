package com.swef.cookcode.admin.dto;

import com.swef.cookcode.user.domain.Authority;
import com.swef.cookcode.user.domain.User;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class PermissionResponse {
    private final Long userId;

    private final Authority authority;

    private final LocalDateTime createdAt;

    public static PermissionResponse from(User user) {
        return PermissionResponse.builder()
                .userId(user.getId())
                .authority(user.getStatus().getAuthority())
                .createdAt(user.getUpdatedAt())
                .build();
    }
}
