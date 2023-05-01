package com.swef.cookcode.fridge.repository;

import com.swef.cookcode.fridge.domain.FridgeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FridgeIngredientRepository extends JpaRepository<FridgeIngredient, Long> {

}
