package com.swef.cookcode.cookie.domain;

import com.swef.cookcode.common.entity.BaseEntity;
import com.swef.cookcode.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.ConnectionBuilder;

@Entity
@Table(name = "cookie_like",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "cookie_id"})
        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CookieLike extends BaseEntity {

    @Id
    @Column(name = "cookie_like_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cookie_id")
    private Cookie cookie;

    private CookieLike(User user, Cookie cookie) {
        this.user = user;
        this.cookie = cookie;
    }

    public static CookieLike createEntity(User user, Cookie cookie){
        return new CookieLike(user, cookie);
    }

}
