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

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @EntityGraph(
            attributePaths = {"author", "steps"}, type = EntityGraphType.FETCH
    )
    Optional<Recipe> findAllElementsById(Long id);

    boolean existsById(Long recipeId);

    @Query(value = "select distinct r from Recipe r join fetch r.author ",
            countQuery = "select count(r) from Recipe r")
    Page<Recipe> findRecipes(Pageable pageable);

    @Query(value = "select new com.swef.cookcode.recipe.dto.response.RecipeResponse(r, "
            + "case when count(distinct ri.ingredient.id) = sum(case when fi.ingred.id is not null then 1 else 0 end) then true else false end ) "
            + "from Recipe r join fetch User u on r.author.id = u.id "
            + "left join r.ingredients ri on ri.isNecessary = true "
            + "left join FridgeIngredient fi on fi.ingred.id = ri.ingredient.id "
            + "left join Fridge f on fi.fridge.id = f.id and f.owner.id = :userId "
            + "group by r.id"
    )
    Page<RecipeResponse> findRecipesWithCookable(@Param("userId") Long userId, Pageable pageable);
}
