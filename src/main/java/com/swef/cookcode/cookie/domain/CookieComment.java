package com.swef.cookcode.cookie.domain;

import com.swef.cookcode.common.entity.Comment;
import com.swef.cookcode.user.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cookie_comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CookieComment extends Comment {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cookie_id")
    private Cookie cookie;

    public CookieComment(User user, Cookie cookie, String comment) {
        super(user, comment);
        this.cookie = cookie;
    }

    public static CookieComment createEntity(User user, Cookie cookie, String comment) {
        return new CookieComment(user, cookie, comment);
    }
}
