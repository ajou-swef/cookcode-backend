package com.swef.cookcode.fridge.domain;

import com.swef.cookcode.common.entity.BaseEntity;
import com.swef.cookcode.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fridge")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Fridge extends BaseEntity {

    private static final int MAX_TITLE_LENGTH = 30;

    @Id
    @Column(name = "fridge_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;

    @Builder
    public Fridge(User owner) {
        this.owner = owner;
    }
}
