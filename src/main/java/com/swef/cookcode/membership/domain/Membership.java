package com.swef.cookcode.membership.domain;

import com.swef.cookcode.common.entity.BaseEntity;
import com.swef.cookcode.membership.dto.MembershipCreateRequest;
import com.swef.cookcode.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "membership")
@Getter
public class Membership extends BaseEntity {

    @Id
    @Column(name = "membership_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creater_id")
    private User creater;

    @Column(name = "grade")
    private String grade;

    @Column(name = "price")
    private Long price;

    private Membership(User creater, String grade, Long price) {
        this.creater = creater;
        this.grade = grade;
        this.price = price;
    }

    public static Membership createEntity(User creater, MembershipCreateRequest request) {
        return new Membership(creater, request.getGrade(), request.getPrice());
    }
}
