package com.swef.cookcode.cookie.domain;

import com.swef.cookcode.common.entity.BaseEntity;
import com.swef.cookcode.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cookie_report")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CookieReport extends BaseEntity {

    private static final int MAX_REPORT_LENGTH = 300;

    @Id
    @Column(name = "cookie_report_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cookie_id")
    private Cookie cookie;

    @Column(name = "report", nullable = false, length = MAX_REPORT_LENGTH)
    private String report;
}

