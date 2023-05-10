package com.swef.cookcode.recipe.repository;

import com.swef.cookcode.recipe.domain.Step;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StepRepository extends JpaRepository<Step, Long> {

    void deleteByRecipeId(Long recipeId);
}
