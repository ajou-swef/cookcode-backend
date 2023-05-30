package com.swef.cookcode.recipe.domain;

import com.swef.cookcode.common.entity.BaseEntity;
import com.swef.cookcode.fridge.domain.Ingredient;
import com.swef.cookcode.recipe.dto.request.RecipeCreateRequest;
import com.swef.cookcode.recipe.dto.request.RecipeUpdateRequest;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "recipe")
@Getter
public class Recipe extends BaseEntity {

    public static final String RECIPE_DIRECTORY_NAME = "recipe";

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

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Step> steps = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true,  fetch = FetchType.LAZY)
    private List<RecipeIngred> ingredients = new ArrayList<>();


    // TODO : Recipe Field Validation
    @Builder
    public Recipe(String title, String description, String thumbnail, User user) {
        this.title = title;
        this.description = description;
        this.thumbnail = thumbnail;
        this.author = user;
    }

    public void addStep(Step step) {
        this.getSteps().add(step);
        if (!step.getRecipe().equals(this)) {
            step.setRecipe(this);
        }
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void clearSteps() {
        this.steps.clear();
    }

    public List<RecipeIngred> getNecessaryIngredients() {
        return ingredients.stream().filter(RecipeIngred::getIsNecessary).toList();
    }

    public List<RecipeIngred> getOptionalIngredients() {
        return ingredients.stream().filter(ri -> !ri.getIsNecessary()).toList();
    }

    public static Recipe createEntity(User user, RecipeCreateRequest request) {
        return  Recipe.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .thumbnail(request.getThumbnail())
                .build();
    }

    public static Recipe updateEntity(Recipe recipe, RecipeUpdateRequest request) {
        recipe.setTitle(request.getTitle());
        recipe.setDescription(request.getDescription());
        recipe.setThumbnail(request.getThumbnail());
        return recipe;
    }

}
