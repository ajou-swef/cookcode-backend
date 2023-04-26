package com.swef.cookcode.user.dto.response;

import com.swef.cookcode.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignUpResponse {

  private final Long userId;

  private final String email;

  private final String name;

  public static SignUpResponse from(User user) {
    return SignUpResponse.builder()
        .userId(user.getId())
        .email(user.getEmail())
        .name(user.getNickname())
        .build();
  }
}
