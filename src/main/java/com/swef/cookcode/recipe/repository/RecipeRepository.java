package com.swef.cookcode.recipe.repository;

import com.swef.cookcode.recipe.domain.Recipe;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @EntityGraph(
            attributePaths = {"author", "steps", "steps.photos", "steps.videos"}
    )
    Optional<Recipe> findAllById(Long id);

    boolean existsById(Long recipeId);

}
