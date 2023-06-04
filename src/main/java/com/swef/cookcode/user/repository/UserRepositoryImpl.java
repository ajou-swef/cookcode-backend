package com.swef.cookcode.user.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swef.cookcode.user.dto.response.UserDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import static com.querydsl.sql.SQLExpressions.count;
import static com.swef.cookcode.user.domain.QSubscribe.subscribe;
import static com.swef.cookcode.user.domain.QUser.user;


@RequiredArgsConstructor
public class UserRepositoryImpl implements UserCustomRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public UserDetailResponse getInfoByUserId(Long userId, Long targetUserId){
        return selectUserAndSubscribes(userId)
                .from(user)
                .where(user.id.eq(targetUserId))
                .fetchOne();
    }

    @Override
    public Slice<UserDetailResponse> findByNicknameContaining(Long userId, String searchQuery, Pageable pageable) {
        return new SliceImpl<>(
                selectUserAndSubscribes(userId)
                        .from(user)
                        .where(user.nickname.contains(searchQuery))
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch()
        );
    }

    @Override
        public Slice<UserDetailResponse> findSubscribers(Pageable pageable, Long userId){
        return new SliceImpl<>(
                queryFactory.select(Projections.constructor(UserDetailResponse.class,
                            user,
                            Expressions.constant(0L),
                            Expressions.constant(0L)))
                    .from(subscribe)
                    .join(user)
                    .on(user.id.eq(subscribe.subscriber.id))
                    .where(subscribe.publisher.id.eq(userId))
                    .fetch()
        );
    }

    @Override
    public Slice<UserDetailResponse> findPublishers(Pageable pageable, Long userId){
        return new SliceImpl<>(
                queryFactory.select(Projections.constructor(UserDetailResponse.class,
                                user,
                                Expressions.constant(1L),
                                Expressions.constant(0L)))
                        .from(subscribe)
                        .join(user)
                        .on(user.id.eq(subscribe.publisher.id))
                        .where(subscribe.subscriber.id.eq(userId))
                        .fetch()
        );
    }

    private JPAQuery<UserDetailResponse> selectUserAndSubscribes(Long userId) {
        return queryFactory.select(Projections.constructor(UserDetailResponse.class,
                user,
                selectIsSubscribed(userId, user.id),
                selectSubscribeCount(user.id)));
    }

    private JPQLQuery<Long> selectIsSubscribed(Long userId, NumberPath<Long> targetUserId) {
        return queryFactory.select(count())
                .from(subscribe)
                .where(subscribe.subscriber.id.eq(userId)
                        .and(subscribe.publisher.id.eq(targetUserId)));
    }

    private JPQLQuery<Long> selectSubscribeCount(NumberPath<Long> targetUserId) {
        return queryFactory.select(count())
                .from(subscribe)
                .where(subscribe.publisher.id.eq(targetUserId));
    }
}
