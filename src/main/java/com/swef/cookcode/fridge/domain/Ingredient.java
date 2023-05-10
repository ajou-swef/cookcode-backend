package com.swef.cookcode.fridge.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ingredient")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Ingredient{

    private static final int MAX_NAME_LENGTH = 10;
    private static final int MAX_CATEGORY_LENGTH = 10;
    private static final int MAX_THUMBNAIL_LENGTH = 300;

    @Id
    @Column(name = "ingred_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name",  nullable = false, length = MAX_NAME_LENGTH)
    private String name;

    @Column(name = "thumbnail", nullable = false, length = MAX_THUMBNAIL_LENGTH)
    private String thumbnail;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = MAX_CATEGORY_LENGTH)
    private Category category;

    @Builder
    Ingredient(String name, String thumbnail, Category category) {
        this.name = name;
        this.thumbnail = thumbnail;
        this.category = category;
    }

}
