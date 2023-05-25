package com.swef.cookcode.cookie.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swef.cookcode.common.Util;
import com.swef.cookcode.cookie.domain.QCookie;
import com.swef.cookcode.cookie.dto.CookieResponse;
import com.swef.cookcode.user.domain.QUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@RequiredArgsConstructor
public class CookieRepositoryImpl implements CookieCustomRepository{

    private final JPAQueryFactory queryFactory;

    private final QCookie cookie = QCookie.cookie;

    @Override
    public Slice<CookieResponse> searchCookies(String query, Pageable pageable) {
        List<CookieResponse> responses = queryFactory.select(Projections.constructor(CookieResponse.class,
                cookie))
                .from(cookie)
                .join(cookie.user)
                .fetchJoin()
                .where(cookieSearchContains(query))
                .fetch();

        return new SliceImpl<>(responses, pageable, Util.hasNextInSlice(responses, pageable));
    }

    private BooleanExpression cookieSearchContains(String query) {
        return cookie.title.containsIgnoreCase(query)
                .or(cookie.description.containsIgnoreCase(query))
                .or(cookie.user.nickname.containsIgnoreCase(query));
    }
}
