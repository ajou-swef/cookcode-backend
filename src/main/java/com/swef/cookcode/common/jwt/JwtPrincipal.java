package com.swef.cookcode.common.jwt;

import static com.swef.cookcode.common.ErrorCode.EMAIL_REQUIRED;
import static com.swef.cookcode.common.ErrorCode.USER_PARAM_REQUIRED;
import static java.util.Objects.isNull;
import static org.springframework.util.StringUtils.hasText;

import com.swef.cookcode.common.error.exception.InvalidRequestException;
import com.swef.cookcode.user.domain.User;
import java.security.Principal;
import lombok.Getter;

@Getter
public class JwtPrincipal implements Principal {

  private final String accessToken;

  private final User user;

  public JwtPrincipal(String accessToken, User user) {
    if(!hasText(accessToken)) throw new InvalidRequestException(EMAIL_REQUIRED);
    if(isNull(user)) throw new InvalidRequestException(USER_PARAM_REQUIRED);

    this.accessToken = accessToken;
    this.user = user;
  }

  @Override
  public String getName() {
    return user.getId().toString();
  }
}
