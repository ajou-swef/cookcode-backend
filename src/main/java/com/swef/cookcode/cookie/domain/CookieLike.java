package com.swef.cookcode.cookie.domain;

import com.swef.cookcode.common.entity.BaseEntity;
import com.swef.cookcode.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cookie_like")
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
}
