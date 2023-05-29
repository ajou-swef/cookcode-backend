package com.swef.cookcode.cookie.domain;

import com.swef.cookcode.common.entity.BaseEntity;
import com.swef.cookcode.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cookie_comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CookieComment extends BaseEntity {

    private static final int MAX_COMMENT_LENGTH = 300;

    @Id
    @Column(name = "cookie_comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cookie_id")
    private Cookie cookie;

    @Column(name = "comment", nullable = false, length = MAX_COMMENT_LENGTH)
    private String comment;

    private CookieComment(User user, Cookie cookie, String comment) {
        this.user = user;
        this.cookie = cookie;
        this.comment = comment;
    }

    public static CookieComment createEntity(User user, Cookie cookie, String comment) {
        return new CookieComment(user, cookie, comment);
    }
}
