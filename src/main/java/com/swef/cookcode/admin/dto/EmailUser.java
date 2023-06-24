package com.swef.cookcode.admin.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EmailUser {
    private String nickname;

    private String authority;

    private String email;

    public static EmailUser createEmailUser(String nickname, String authority, String email) {
        return EmailUser.builder()
                .nickname(nickname)
                .authority(authority)
                .email(email)
                .build();
    }
}
