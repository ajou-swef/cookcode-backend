package com.swef.cookcode.user.domain;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
public enum Authority {

    ADMIN("관리자", 2),
    USER("사용자", 0),
    INFLUENCER("인플루언서", 1);

    private final String consoleValue;

    private final Integer priority;

    Authority(String consoleValue, Integer priority) {
        this.consoleValue = consoleValue;
        this.priority = priority;
    }

    public SimpleGrantedAuthority toGrantedAuthority() {
        return new SimpleGrantedAuthority("ROLE_" + this.toString());
    }
}
