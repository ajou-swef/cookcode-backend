package com.swef.cookcode.fridge.repository;

import com.swef.cookcode.fridge.domain.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient, Long>, IngredientCustomRepository {
}
