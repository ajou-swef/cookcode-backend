package com.swef.cookcode.recipe.domain;

import com.swef.cookcode.common.entity.Comment;
import com.swef.cookcode.user.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recipe_comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RecipeComment extends Comment {

    private static final int MAX_COMMENT_LENGTH = 500;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    public RecipeComment(Recipe recipe, User user, String comment) {
        super(user, comment);
        this.recipe = recipe;
    }

}
