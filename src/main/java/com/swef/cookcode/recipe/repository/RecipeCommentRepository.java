package com.swef.cookcode.recipe.repository;

import com.swef.cookcode.recipe.domain.RecipeComment;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RecipeCommentRepository extends JpaRepository<RecipeComment, Long> {
    @Query("select rc from RecipeComment rc join fetch rc.user where rc.recipe.id = :recipeId")
    Slice<RecipeComment> findRecipeComments(@Param("recipeId") Long recipeId, Pageable pageable);

    @Modifying
    @Query("delete from RecipeComment rc where rc.recipe.id = :recipeId")
    void deleteAllByRecipeId(@Param("recipeId") Long recipeId);
}
