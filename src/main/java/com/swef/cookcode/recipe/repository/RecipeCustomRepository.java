package com.swef.cookcode.recipe.repository;

import com.swef.cookcode.recipe.dto.response.RecipeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface RecipeCustomRepository {
    Slice<RecipeResponse> findRecipes(Long userId, Boolean isCookable, Pageable pageable);

    Slice<RecipeResponse> searchRecipes(Long userId, String query, Boolean isCookable, Pageable pageable);

    Slice<RecipeResponse> findRecipesOfUser(Long userId, Long targetUserId, Pageable pageable);
}
