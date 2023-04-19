package com.swef.cookcode.fridge.domain;

import com.swef.cookcode.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ingredient")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Ingredient extends BaseEntity {

    @Id
    @Column(name = "ingred_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ingredId;

    @Column(name = "name")
    private String name;

    @Column(name = "thumbnail")
    private String thumbnail;

    @Column(name = "category")
    private String category;

//    식재료에서 냉장고를 조회할 일이 없을듯
//    @OneToMany(mappedBy = "ingred")
//    private List<FridgeIngredient> fridges = new ArrayList<>();

}
