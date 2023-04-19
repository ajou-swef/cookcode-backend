package com.swef.cookcode.fridge.domain;

import com.swef.cookcode.common.entity.BaseEntity;
import com.swef.cookcode.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fridge")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Fridge extends BaseEntity {

    private static final int MAX_TITLE_LENGTH = 30;

    @Id
    @Column(name = "fridge_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int fridgeId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;
}
