package com.swef.cookcode.fridge.repository;

import com.swef.cookcode.fridge.domain.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngerdientRepository extends JpaRepository<Ingredient, Long> {

}
