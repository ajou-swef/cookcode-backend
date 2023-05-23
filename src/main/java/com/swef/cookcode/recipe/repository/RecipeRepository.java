package com.swef.cookcode.recipe.repository;

import com.swef.cookcode.recipe.domain.Recipe;
import com.swef.cookcode.recipe.dto.response.RecipeResponse;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecipeRepository extends JpaRepository<Recipe, Long>, RecipeCustomRepository {

    @EntityGraph(
            attributePaths = {"author", "steps"}, type = EntityGraphType.FETCH
    )
    Optional<Recipe> findAllElementsById(Long id);

    boolean existsById(Long recipeId);

}
