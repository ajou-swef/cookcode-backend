package com.swef.cookcode.recipe.repository;

import com.swef.cookcode.recipe.domain.RecipeIngred;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeIngredRepository extends JpaRepository<RecipeIngred, Long> {
}
