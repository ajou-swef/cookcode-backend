package com.swef.cookcode.user.domain;

import lombok.Getter;

@Getter
public enum Status {
    BLOCKED(null),
    VALID(null),
    INF_REQUESTED(Authority.INFLUENCER),

    ADM_REQUESTED(Authority.ADMIN),

    QUIT(null);

    private final Authority authority;


    Status(Authority authority) {
        this.authority = authority;
    }
}
