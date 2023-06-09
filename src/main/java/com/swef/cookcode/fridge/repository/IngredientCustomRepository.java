package com.swef.cookcode.fridge.repository;

import com.swef.cookcode.fridge.dto.response.IngredSimpleResponse;
import java.util.List;

public interface IngredientCustomRepository {
    List<IngredSimpleResponse> getNecessaryIngredientsForRecipe(Long userId, Long recipeId);

    List<IngredSimpleResponse> getOptionalIngredientsForRecipe(Long userId, Long recipeId);

}
