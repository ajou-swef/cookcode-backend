package com.swef.cookcode.membership.domain;

import com.swef.cookcode.common.entity.BaseEntity;
import com.swef.cookcode.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "membership_join")
@Getter
public class MembershipJoin extends BaseEntity {

    @Id
    @Column(name = "membershipJoin_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membership_id")
    private Membership membership;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User subscriber;

    private MembershipJoin(Membership membership, User subscriber) {
        this.membership = membership;
        this.subscriber = subscriber;
    }

    public static MembershipJoin createEntity(Membership membership, User subscriber) {
        return new MembershipJoin(membership, subscriber);
    }
}
