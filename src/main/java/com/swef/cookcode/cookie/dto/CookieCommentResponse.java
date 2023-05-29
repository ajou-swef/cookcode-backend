package com.swef.cookcode.cookie.dto;

import com.swef.cookcode.cookie.domain.CookieComment;
import com.swef.cookcode.user.dto.response.UserSimpleResponse;
import lombok.Getter;

@Getter
public class CookieCommentResponse {

    private final Long id;

    private final UserSimpleResponse user;

    private final String comment;

    private CookieCommentResponse(Long id, UserSimpleResponse user, String comment){
        this.id = id;
        this.user = user;
        this.comment = comment;
    }

    public static CookieCommentResponse of(CookieComment cookieComment){
        return new CookieCommentResponse(cookieComment.getId(), UserSimpleResponse.from(cookieComment.getUser()), cookieComment.getComment());
    }
}
