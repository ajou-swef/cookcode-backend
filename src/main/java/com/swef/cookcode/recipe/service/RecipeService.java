package com.swef.cookcode.recipe.service;

import com.swef.cookcode.fridge.domain.Ingredient;
import com.swef.cookcode.fridge.dto.IngredientSimpleResponse;
import com.swef.cookcode.fridge.service.IngredientSimpleService;
import com.swef.cookcode.recipe.domain.Recipe;
import com.swef.cookcode.recipe.domain.RecipeIngred;
import com.swef.cookcode.recipe.dto.request.RecipeCreateRequest;
import com.swef.cookcode.recipe.dto.response.RecipeResponse;
import com.swef.cookcode.recipe.dto.response.StepResponse;
import com.swef.cookcode.recipe.repository.RecipeIngredRepository;
import com.swef.cookcode.recipe.repository.RecipeRepository;
import com.swef.cookcode.user.domain.User;
import com.swef.cookcode.user.dto.response.UserSimpleResponse;
import com.swef.cookcode.user.service.UserSimpleService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;

    private final RecipeIngredRepository recipeIngredRepository;
    private final UserSimpleService userSimpleService;

    private final StepService stepService;
    private final IngredientSimpleService ingredientSimpleService;

    @Transactional
    public RecipeResponse createRecipe(User currentUser, RecipeCreateRequest request) {
        Recipe newRecipe = Recipe.builder()
                .user(currentUser)
                .title(request.getTitle())
                .description(request.getDescription())
                .thumbnail(request.getThumbnail())
                .build();
        Recipe savedRecipe = recipeRepository.save(newRecipe);

        // TODO : 필수 재료이면서 선택 재료인 것들에 대해 예외 처리하는 비즈니스 로직 추가 필요
        List<Ingredient> requiredIngredients = ingredientSimpleService.getIngredientsByIds(request.getIngredients());
        List<Ingredient> optionalIngredients = ingredientSimpleService.getIngredientsByIds(request.getOptionalIngredients());
        List<IngredientSimpleResponse> ingredResponses = requiredIngredients.stream().map(
                IngredientSimpleResponse::from).toList();
        List<IngredientSimpleResponse> optionalIngredResponses = optionalIngredients.stream().map(
                IngredientSimpleResponse::from).toList();

        saveIngredientsOfRecipe(savedRecipe, requiredIngredients, true);
        saveIngredientsOfRecipe(savedRecipe, optionalIngredients, false);

        List<StepResponse> stepResponses = stepService.saveStepsForRecipe(savedRecipe, request.getSteps());

        return RecipeResponse.builder()
                .recipeId(savedRecipe.getId())
                .title(savedRecipe.getTitle())
                .description(savedRecipe.getDescription())
                .thumbnail(savedRecipe.getThumbnail())
                .createdAt(savedRecipe.getCreatedAt())
                .updatedAt(savedRecipe.getUpdatedAt())
                .ingredients(ingredResponses)
                .optionalIngredients(optionalIngredResponses)
                .steps(stepResponses)
                .user(UserSimpleResponse.from(currentUser))
                .build();
    }

    @Transactional
    void saveIngredientsOfRecipe(Recipe recipe, List<Ingredient> ingredients, Boolean isNecessary) {
        List<RecipeIngred> recipeIngredList = ingredients.stream()
                .map(ingredient -> new RecipeIngred(recipe, ingredient, isNecessary)).toList();
        recipeIngredRepository.saveAll(recipeIngredList);
    }
}
