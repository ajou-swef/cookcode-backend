package com.swef.cookcode.recipe.domain;

import com.swef.cookcode.common.entity.BaseEntity;
import com.swef.cookcode.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "recipe")
@Getter
public class Recipe extends BaseEntity {

    private static final int MAX_TITLE_LENGTH = 30;

    private static final int MAX_DESCRIPTION_LENGTH = 500;

    private static final int MAX_THUMBNAIL_LENGTH = 300;

    @Id
    @Column(name = "recipe_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = MAX_TITLE_LENGTH)
    private String title;

    @Column(nullable = false, length = MAX_DESCRIPTION_LENGTH)
    private String description;

    @Column(nullable = false, length = MAX_THUMBNAIL_LENGTH)
    private String thumbnail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    // TODO : Recipe Field Validation
    @Builder
    public Recipe(String title, String description, String thumbnail, User user) {
        this.title = title;
        this.description = description;
        this.thumbnail = thumbnail;
        this.author = user;
    }
}
