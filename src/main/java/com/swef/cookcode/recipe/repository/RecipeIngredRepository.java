package com.swef.cookcode.recipe.repository;

import com.swef.cookcode.fridge.domain.Ingredient;
import com.swef.cookcode.recipe.domain.RecipeIngred;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface RecipeIngredRepository extends JpaRepository<RecipeIngred, Long> {

    @Query("select i from RecipeIngred r join fetch Ingredient i on i.id = r.ingredient.id where r.recipe.id = :recipeId and r.isNecessary = :isNecessary")
    List<Ingredient> findByRecipeIdAndIsNecessary(@Param("recipeId") Long recipeId, @Param("isNecessary") Boolean isNecessary);


    @Modifying
    @Query("delete from RecipeIngred r where r.recipe.id = :recipeId")
    void deleteByRecipeId(@Param("recipeId") Long recipeId);
}
