package com.swef.cookcode.recipe.repository;

import com.swef.cookcode.recipe.dto.response.RecipeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecipeCustomRepository {
    Page<RecipeResponse> findRecipes(Long userId, Boolean isCookable, Pageable pageable);
}
