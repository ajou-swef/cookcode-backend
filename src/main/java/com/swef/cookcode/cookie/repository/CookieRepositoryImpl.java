package com.swef.cookcode.cookie.repository;

import static com.swef.cookcode.cookie.domain.QCookie.cookie;
import static com.swef.cookcode.cookie.domain.QCookieComment.cookieComment;
import static com.swef.cookcode.cookie.domain.QCookieLike.cookieLike;
import static com.swef.cookcode.recipe.domain.QRecipe.recipe;
import static com.swef.cookcode.user.domain.QUser.user;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swef.cookcode.cookie.dto.CookieResponse;
import com.swef.cookcode.common.util.Util;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;


@RequiredArgsConstructor
public class CookieRepositoryImpl implements CookieCustomRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<CookieResponse> searchCookies(String query, Long userId, Pageable pageable) {
        List<CookieResponse> responses = selectCookieResponseFromCookieUserJoinAndLikeComment(userId)
                .where(cookieSearchContains(query))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return new SliceImpl<>(responses, pageable, Util.hasNextInSlice(responses, pageable));
    }

    private BooleanExpression cookieSearchContains(String query) {
        return cookie.title.containsIgnoreCase(query)
                .or(cookie.description.containsIgnoreCase(query))
                .or(cookie.user.nickname.containsIgnoreCase(query));
    }

    @Override
    public Slice<CookieResponse> findByTargetUserId(Pageable pageable, Long targetUserId, Long userId) {
        List<CookieResponse> responses = selectCookieResponseFromCookieUserJoinAndLikeComment(userId)
                .where(user.id.eq(targetUserId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();
        return new SliceImpl<>(responses, pageable, Util.hasNextInSlice(responses, pageable));
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
                        cookie,
                        isLikedExpression(userId),
                        selectCookieLikeCount(),
                        selectCookieCommentCount()))
                .from(cookie)
                .join(cookie.user)
                .fetchJoin()
                .leftJoin(recipe).on(recipe.id.eq(cookie.recipe.id));
    }

    private BooleanExpression isLikedExpression(Long userId) {
        return new CaseBuilder()
                .when(selectIsCookieLikedByUser(userId).eq(0L)).then(false).otherwise(true);
    }

    private JPQLQuery<Long> selectIsCookieLikedByUser(Long userId){
        return JPAExpressions.select(cookieLike.count())
                .from(cookieLike)
                .where(cookieLike.cookie.id.eq(cookie.id)
                        .and(cookieLike.user.id.eq(userId)));
    }

    private JPQLQuery<Long> selectCookieLikeCount(){
        return JPAExpressions.select(cookieLike.count())
                .from(cookieLike)
                .where(cookieLike.cookie.id.eq(cookie.id));
    }

    private JPQLQuery<Long> selectCookieCommentCount(){
        return JPAExpressions.select(cookieComment.count())
                .from(cookieComment)
                .where(cookieComment.cookie.id.eq(cookie.id));
    }
}
