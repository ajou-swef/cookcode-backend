package com.swef.cookcode.recipe.repository;

import com.swef.cookcode.recipe.domain.Recipe;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @EntityGraph(
            attributePaths = {"author", "steps"}, type = EntityGraphType.FETCH
    )
    Optional<Recipe> findAllElementsById(Long id);

    boolean existsById(Long recipeId);

    @Query(value = "select distinct r from Recipe r join fetch r.author ",
            countQuery = "select count(r) from Recipe r")
    Page<Recipe> findRecipes(Pageable pageable);
}
