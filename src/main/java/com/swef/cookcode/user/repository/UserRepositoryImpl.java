package com.swef.cookcode.user.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swef.cookcode.user.domain.User;
import com.swef.cookcode.user.dto.response.UserDetailResponse;
import lombok.RequiredArgsConstructor;

import static com.swef.cookcode.user.domain.QSubscribe.subscribe;
import static com.swef.cookcode.user.domain.QUser.user;


@RequiredArgsConstructor
public class UserRepositoryImpl implements UserCustomRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public UserDetailResponse getInfoByUserId(Long userId, Long targetUserId){
        return queryFactory.select(Projections.constructor(UserDetailResponse.class,
                user,
                selectIsSubscribed(userId, targetUserId),
                selectSubscribeCount(targetUserId)))
            .from(user)
            .where(user.id.eq(targetUserId))
            .fetchOne();
    }

    private JPQLQuery<Long> selectIsSubscribed(Long userId, Long targetUserId) {
        return queryFactory.select(subscribe.count())
                .from(subscribe)
                .where(subscribe.subscriber.id.eq(userId)
                        .and(subscribe.publisher.id.eq(targetUserId)));
    }

    private JPQLQuery<Long> selectSubscribeCount(Long targetUserId) {
        return queryFactory.select(subscribe.count())
                .from(subscribe)
                .where(subscribe.publisher.id.eq(targetUserId));
    }
}
