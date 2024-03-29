package com.swef.cookcode.user.repository;

import static com.querydsl.sql.SQLExpressions.count;
import static com.swef.cookcode.user.domain.QSubscribe.subscribe;
import static com.swef.cookcode.user.domain.QUser.user;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swef.cookcode.common.util.QueryUtil;
import com.swef.cookcode.common.util.Util;
import com.swef.cookcode.user.domain.QSubscribe;
import com.swef.cookcode.user.dto.response.UserDetailResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;


// TODO : NumberPath 매개변수로 엮인 함수끼리의 의존성 refactor
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserCustomRepository{

    private final JPAQueryFactory queryFactory;

    private final QSubscribe subscribeForFlag = new QSubscribe("subscribeForFlag");

    private final QSubscribe subscribeForCount = new QSubscribe("subscribeForFlag");

    @Override
    public UserDetailResponse getInfoByUserId(Long userId, Long targetUserId){
        return selectUserWithSubscribes(userId)
                .from(user)
                .where(user.id.eq(targetUserId))
                .fetchOne();
    }

    @Override
    public Slice<UserDetailResponse> findByNicknameContaining(Long userId, String searchQuery, Pageable pageable) {
          List<UserDetailResponse> responses = selectUserWithSubscribes(userId)
                        .from(user)
                        .where(user.nickname.contains(searchQuery))
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize()+1)
                        .orderBy(QueryUtil.getOrderSpecifiers(pageable.getSort(), List.of(selectSubscribeCount(user.id)), user.createdAt))
                        .fetch();
        return new SliceImpl<>(responses, pageable, Util.hasNextInSlice(responses, pageable));
    }


    @Override
        public Slice<UserDetailResponse> findSubscribers(Pageable pageable, Long userId){
        List<UserDetailResponse> responses = queryFactory.select(Projections.constructor(UserDetailResponse.class,
                                user,
                                isSubscribedExpression(userId, user.id),
                                selectSubscribeCount(user.id)))
                        .from(subscribe)
                        .join(user)
                        .on(user.id.eq(subscribe.subscriber.id))
                        .where(subscribe.publisher.id.eq(userId))
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize()+1)
                        .orderBy(QueryUtil.getOrderSpecifiers(pageable.getSort(), List.of(selectSubscribeCount(user.id)), subscribe.createdAt))
                        .fetch();
        return new SliceImpl<>(responses, pageable, Util.hasNextInSlice(responses, pageable));
    }

    @Override
    public Slice<UserDetailResponse> findPublishers(Pageable pageable, Long userId){
        List<UserDetailResponse> responses =  queryFactory.select(Projections.constructor(UserDetailResponse.class,
                            user,
                            Expressions.TRUE,
                            selectSubscribeCount(user.id)))
                    .from(subscribe)
                    .join(user)
                    .on(user.id.eq(subscribe.publisher.id))
                    .where(subscribe.subscriber.id.eq(userId))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize()+1)
                    .orderBy(QueryUtil.getOrderSpecifiers(pageable.getSort(), List.of(selectSubscribeCount(user.id)), subscribe.createdAt))
                    .fetch();
        return new SliceImpl<>(responses, pageable, Util.hasNextInSlice(responses, pageable));
    }

    private JPAQuery<UserDetailResponse> selectUserWithSubscribes(Long userId) {
        return queryFactory.select(Projections.constructor(UserDetailResponse.class,
                user,
                isSubscribedExpression(userId, user.id),
                selectSubscribeCount(user.id)));
    }

    private BooleanExpression isSubscribedExpression(Long userId, NumberPath<Long> targetUserId) {
        return new CaseBuilder()
                .when(selectIsSubscribed(userId, targetUserId).eq(0L))
                .then(false)
                .otherwise(true);
    }

    private JPQLQuery<Long> selectIsSubscribed(Long userId, NumberPath<Long> targetUserId) {
        return queryFactory.select(count())
                .from(subscribeForFlag)
                .where(subscribeForFlag.subscriber.id.eq(userId)
                        .and(subscribeForFlag.publisher.id.eq(targetUserId)));
    }

    private JPQLQuery<Long> selectSubscribeCount(NumberPath<Long> targetUserId) {
        return queryFactory.select(count())
                .from(subscribeForCount)
                .where(subscribeForCount.publisher.id.eq(targetUserId));
    }
}
