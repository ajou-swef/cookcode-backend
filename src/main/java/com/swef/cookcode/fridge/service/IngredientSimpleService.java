package com.swef.cookcode.fridge.service;

import com.swef.cookcode.common.ErrorCode;
import com.swef.cookcode.common.error.exception.NotFoundException;
import com.swef.cookcode.fridge.domain.Ingredient;
import com.swef.cookcode.fridge.repository.IngredientRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IngredientSimpleService {

    private final IngredientRepository ingredientRepository;

    public List<Ingredient> getIngredientsByIds(List<Long> ids) {
        try{
            List<Ingredient> ingredients = ingredientRepository.findAllById(ids);
            if (ingredients.size() != ids.size()) throw new IllegalArgumentException();
            return ingredients;
        } catch(IllegalArgumentException exception) {
            throw new NotFoundException(ErrorCode.INGREDIENT_NOT_FOUND);
        }
    }
}
