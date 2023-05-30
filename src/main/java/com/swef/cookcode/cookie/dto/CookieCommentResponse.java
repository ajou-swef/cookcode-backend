package com.swef.cookcode.cookie.dto;

import com.swef.cookcode.cookie.domain.CookieComment;
import com.swef.cookcode.user.dto.response.UserSimpleResponse;
import lombok.Getter;

@Getter
public class CookieCommentResponse {

    private final Long id;

    private final UserSimpleResponse user;

    private final String comment;

    private CookieCommentResponse(CookieComment cookieComment){
        this.id = cookieComment.getId();
        this.user = UserSimpleResponse.from(cookieComment.getUser());
        this.comment = cookieComment.getComment();
    }

    public static CookieCommentResponse of(CookieComment cookieComment){
        return new CookieCommentResponse(cookieComment);
    }
}
