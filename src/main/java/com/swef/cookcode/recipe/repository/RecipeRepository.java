package com.swef.cookcode.recipe.repository;

import com.swef.cookcode.recipe.domain.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {



}
