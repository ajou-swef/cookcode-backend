package com.swef.cookcode.user.domain;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum Authority {

    ADMIN,
    USER,
    INFLUENCER;

    public SimpleGrantedAuthority toGrantedAuthority() {
        return new SimpleGrantedAuthority(this.toString());
    }
}
