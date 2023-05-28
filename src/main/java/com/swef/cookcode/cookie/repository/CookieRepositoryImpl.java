package com.swef.cookcode.cookie.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swef.cookcode.cookie.domain.QCookie;
import com.swef.cookcode.cookie.domain.QCookieComment;
import com.swef.cookcode.cookie.domain.QCookieLike;
import com.swef.cookcode.cookie.dto.CookieResponse;
import com.swef.cookcode.user.domain.QUser;
import com.swef.cookcode.user.dto.response.UserSimpleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;


@RequiredArgsConstructor
public class CookieRepositoryImpl implements CookieCustomRepository{

    private final JPAQueryFactory queryFactory;

    private final QCookie cookie = QCookie.cookie;

    private final QUser user = QUser.user;

    private final QCookieLike cookieLike = QCookieLike.cookieLike;

    private final QCookieComment cookieComment = QCookieComment.cookieComment;

    @Override
    public Slice<CookieResponse> findByTargetUserId(Pageable pageable, Long targetUserId, Long userId) {
        return new SliceImpl<>(
            selectCookieResponseFromCookieUserJoinAndLikeComment(userId)
                .where(user.id.eq(targetUserId))
                .offset((long) pageable.getPageNumber() * pageable.getPageSize())
                .limit(pageable.getPageSize())
                .fetch()
        );
    }

    @Override
    public CookieResponse findCookieResponseById(Long cookieId, Long userId) {
        return selectCookieResponseFromCookieUserJoinAndLikeComment(userId)
            .where(cookie.id.eq(cookieId))
            .fetchOne();
    }

    @Override
    public List<CookieResponse> findRandomCookieResponse(Pageable pageable, Long userId) {

        long totalRowCount = queryFactory.selectFrom(cookie).stream().count();

        int randomOffset = (int) (Math.random() * totalRowCount);

        return selectCookieResponseFromCookieUserJoinAndLikeComment(userId)
            .offset(randomOffset)
            .limit(pageable.getPageSize())
            .fetch();
    }

    private JPAQuery<CookieResponse> selectCookieResponseFromCookieUserJoinAndLikeComment(Long userId){
        return queryFactory.select(Projections.constructor(CookieResponse.class,
                        cookie.id,
                        cookie.title,
                        cookie.description,
                        cookie.videoUrl,
                        cookie.createdAt,
                        Projections.constructor(UserSimpleResponse.class,
                                user.id,
                                user.profileImage,
                                user.nickname),
                        JPAExpressions.select(cookieLike.count()).from(cookieLike).where(cookieLike.cookie.id.eq(cookie.id).and(cookieLike.user.id.eq(userId))),
                        JPAExpressions.select(cookieLike.count()).from(cookieLike).where(cookieLike.cookie.id.eq(cookie.id)),
                        JPAExpressions.select(cookieComment.count()).from(cookieComment).where(cookieComment.cookie.id.eq(cookie.id))))
                .from(cookie)
                .join(cookie.user, user);
    }
}
