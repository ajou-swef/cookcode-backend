package com.swef.cookcode.user.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swef.cookcode.user.dto.response.UserDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import static com.swef.cookcode.user.domain.QSubscribe.subscribe;
import static com.swef.cookcode.user.domain.QUser.user;


@RequiredArgsConstructor
public class UserRepositoryImpl implements UserCustomRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public UserDetailResponse getInfoByUserId(Long userId, Long targetUserId){
        return selectUserAndSubscribesFromUser(userId)
            .where(user.id.eq(targetUserId))
            .fetchOne();
    }

    @Override
    public Slice<UserDetailResponse> findByNicknameContaining(Long userId, String searchQuery, Pageable pageable) {
        return new SliceImpl<>(
                selectUserAndSubscribesFromUser(userId)
                    .where(user.nickname.contains(searchQuery))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch()
        );
    }

    private JPAQuery<UserDetailResponse> selectUserAndSubscribesFromUser(Long userId) {
        return queryFactory.select(Projections.constructor(UserDetailResponse.class,
                        user,
                        selectIsSubscribed(userId, user.id),
                        selectSubscribeCount(user.id)))
                .from(user);
    }

    private JPQLQuery<Long> selectIsSubscribed(Long userId, NumberPath<Long> targetUserId) {
        return queryFactory.select(subscribe.count())
                .from(subscribe)
                .where(subscribe.subscriber.id.eq(userId)
                        .and(subscribe.publisher.id.eq(targetUserId)));
    }

    private JPQLQuery<Long> selectSubscribeCount(NumberPath<Long> targetUserId) {
        return queryFactory.select(subscribe.count())
                .from(subscribe)
                .where(subscribe.publisher.id.eq(targetUserId));
    }
}
