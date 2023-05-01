package com.swef.cookcode.fridge.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
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
    private Long id;

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

    @Builder
    public FridgeIngredient(Fridge fridge, Ingredient ingred, String quantity, Date expiredAt) {
        this.fridge = fridge;
        this.ingred = ingred;
        this.quantity = quantity;
        this.expiredAt = expiredAt;
    }
}
