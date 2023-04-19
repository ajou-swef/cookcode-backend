package com.swef.cookcode.fridge.domain;


import com.swef.cookcode.cookie.domain.Cookie;
import com.swef.cookcode.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "fridge_ingred")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class FridgeIngredient {

    private static final int MAX_QUANTITY_LENGTH = 10;

    @Id
    @Column(name = "fridge_ingred_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int fridgeIngredId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fridge_id")
    private Fridge fridge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingred_id")
    private Ingredient ingred;

    @Column(name = "quantity", nullable = false, length = MAX_QUANTITY_LENGTH)
    private String quantity;

    @Column(name = "expired_at", nullable = false)
    private Date expiredAt;

}
