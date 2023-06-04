package com.swef.cookcode.recipe.repository;

import com.swef.cookcode.recipe.domain.RecipeLike;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RecipeLikeRepository extends JpaRepository<RecipeLike, Long> {

    Optional<RecipeLike> findByUserIdAndRecipeId(@Param("userId") Long userId, @Param("recipeId") Long recipeId);


    @Modifying
    @Query("delete from RecipeLike rl where rl.recipe.id = :recipeId")
    void deleteAllByRecipeId(@Param("recipeId") Long recipeId);
}
