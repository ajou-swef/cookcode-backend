package com.swef.cookcode.recipe.domain;

import com.swef.cookcode.fridge.domain.Ingredient;
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
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "recipe_ingred",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unq_ingred_recipe_ingred_id_recipe_id",
                        columnNames = {"ingred_id", "recipe_id"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RecipeIngred {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_ingred_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingred_id")
    private Ingredient ingredient;

    private Boolean isNecessary = true;

    public RecipeIngred(Recipe recipe, Ingredient ingredient, Boolean isNecessary) {
        this.ingredient = ingredient;
        this.recipe = recipe;
        this.isNecessary = isNecessary;
    }
}
