package com.swef.cookcode.recipe.service;

import com.swef.cookcode.common.ErrorCode;
import com.swef.cookcode.common.Util;
import com.swef.cookcode.common.error.exception.NotFoundException;
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

    // TODO : JPA List 사용으로 refactoring
    @Transactional
    public RecipeResponse createRecipe(User currentUser, RecipeCreateRequest request) {
        //Ingredient
        Util.validateDuplication(request.getIngredients(), request.getOptionalIngredients());

        List<Ingredient> requiredIngredients = ingredientSimpleService.getIngredientsByIds(request.getIngredients());
        List<Ingredient> optionalIngredients = ingredientSimpleService.getIngredientsByIds(request.getOptionalIngredients());

        //Recipe 생성
        Recipe newRecipe = Recipe.builder()
                .user(currentUser)
                .title(request.getTitle())
                .description(request.getDescription())
                .thumbnail(request.getThumbnail())
                .build();
        Recipe savedRecipe = recipeRepository.save(newRecipe);

        //Recipe의 Ingredients 생성
        saveIngredientsOfRecipe(savedRecipe, requiredIngredients, true);
        saveIngredientsOfRecipe(savedRecipe, optionalIngredients, false);

        List<IngredientSimpleResponse> ingredResponses = requiredIngredients.stream().map(
                IngredientSimpleResponse::from).toList();
        List<IngredientSimpleResponse> optionalIngredResponses = optionalIngredients.stream().map(
                IngredientSimpleResponse::from).toList();

        //Recipe의 Steps 생성
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

    // TODO : Ingredients, OptionalIngredients fix needed
    @Transactional(readOnly = true)
    public RecipeResponse getRecipeById(Long recipeId) {
        Recipe retrievedRecipe = recipeRepository.findById(recipeId).orElseThrow(() -> new NotFoundException(
                ErrorCode.RECIPE_NOT_FOUND));
        return RecipeResponse.builder()
                .recipeId(retrievedRecipe.getId())
                .title(retrievedRecipe.getTitle())
                .thumbnail(retrievedRecipe.getThumbnail())
                .description(retrievedRecipe.getDescription())
                .steps(retrievedRecipe.getSteps().stream().map(step -> StepResponse.from(step, step.getPhotos(), step.getVideos())).toList())
                .createdAt(retrievedRecipe.getCreatedAt())
                .updatedAt(retrievedRecipe.getUpdatedAt())
                .user(UserSimpleResponse.from(retrievedRecipe.getAuthor()))
                .ingredients(retrievedRecipe.getIngredients().stream().map(stepIngred -> IngredientSimpleResponse.from(stepIngred.getIngredient())).toList())
                .optionalIngredients(retrievedRecipe.getOptionalIngredients().stream().map(stepIngred -> IngredientSimpleResponse.from(stepIngred.getIngredient())).toList())
                .build();
    }

    @Transactional
    void saveIngredientsOfRecipe(Recipe recipe, List<Ingredient> ingredients, Boolean isNecessary) {
        List<RecipeIngred> recipeIngredList = ingredients.stream()
                .map(ingredient -> new RecipeIngred(recipe, ingredient, isNecessary)).toList();
        recipeIngredRepository.saveAll(recipeIngredList);
    }
}
