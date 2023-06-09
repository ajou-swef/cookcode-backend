package com.swef.cookcode.user.domain;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
public enum Authority {

    ADMIN("관리자"),
    USER("사용자"),
    INFLUENCER("인플루언서");

    private final String consoleValue;

    Authority(String consoleValue) {
        this.consoleValue = consoleValue;
    }

    public SimpleGrantedAuthority toGrantedAuthority() {
        return new SimpleGrantedAuthority("ROLE_" + this.toString());
    }
}
