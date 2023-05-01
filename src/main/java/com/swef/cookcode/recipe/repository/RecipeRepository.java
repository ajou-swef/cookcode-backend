package com.swef.cookcode.recipe.repository;

import com.swef.cookcode.recipe.domain.Recipe;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @EntityGraph(
            attributePaths = {"author", "steps", "steps.photos", "steps.videos", "ingredients", "ingredients.ingredient", "optionalIngredients", "optionalIngredients.ingredient"}
    )
    Optional<Recipe> findById(Long id);

}
