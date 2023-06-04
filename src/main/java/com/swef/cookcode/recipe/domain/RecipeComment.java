package com.swef.cookcode.recipe.domain;

import com.swef.cookcode.common.entity.BaseEntity;
import com.swef.cookcode.user.domain.User;
import jakarta.persistence.CascadeType;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recipe_comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RecipeComment extends BaseEntity {

    private static final int MAX_COMMENT_LENGTH = 500;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = MAX_COMMENT_LENGTH)
    private String comment;

    public RecipeComment(Recipe recipe, User user, String comment) {
        this.recipe = recipe;
        this.user = user;
        this.comment = comment;
    }

}
