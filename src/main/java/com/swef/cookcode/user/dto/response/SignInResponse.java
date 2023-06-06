package com.swef.cookcode.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignInResponse {

  private final Long userId;

  private final String accessToken;

  private final String refreshToken;

  public static SignInResponse from(Long userId, String accessToken, String refreshToken) {
    return SignInResponse.builder()
            .userId(userId)
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
  }
}
