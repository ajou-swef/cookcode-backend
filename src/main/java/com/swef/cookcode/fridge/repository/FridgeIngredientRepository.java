package com.swef.cookcode.fridge.repository;

import com.swef.cookcode.fridge.domain.FridgeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FridgeIngredientRepository extends JpaRepository<FridgeIngredient, Long> {

    @Query("SELECT fi FROM FridgeIngredient fi JOIN FETCH fi.ingred WHERE fi.fridge.id = :fridgeId")
    List<FridgeIngredient> findByFridgeId(@Param("fridgeId") Long fridgeId);
}
