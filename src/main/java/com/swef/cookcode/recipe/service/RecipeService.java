package com.swef.cookcode.recipe.service;

import com.swef.cookcode.common.ErrorCode;
import com.swef.cookcode.common.Util;
import com.swef.cookcode.common.error.exception.NotFoundException;
import com.swef.cookcode.common.error.exception.PermissionDeniedException;
import com.swef.cookcode.fridge.domain.Ingredient;
import com.swef.cookcode.fridge.dto.response.IngredSimpleResponse;
import com.swef.cookcode.fridge.service.IngredientSimpleService;
import com.swef.cookcode.recipe.domain.Recipe;
import com.swef.cookcode.recipe.domain.RecipeIngred;
import com.swef.cookcode.recipe.dto.request.RecipeCreateRequest;
import com.swef.cookcode.recipe.dto.request.RecipeUpdateRequest;
import com.swef.cookcode.recipe.dto.response.RecipeResponse;
import com.swef.cookcode.recipe.dto.response.StepResponse;
import com.swef.cookcode.recipe.repository.RecipeIngredRepository;
import com.swef.cookcode.recipe.repository.RecipeRepository;
import com.swef.cookcode.user.domain.User;
import com.swef.cookcode.user.dto.response.UserSimpleResponse;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RecipeRepository recipeRepository;

    private final RecipeIngredRepository recipeIngredRepository;

    private final StepService stepService;
    private final IngredientSimpleService ingredientSimpleService;

    // TODO : JPA List 연관관계 사용으로 refactoring
    @Transactional
    public RecipeResponse createRecipe(User user, RecipeCreateRequest request) {
        Util.validateDuplication(request.getIngredients(), request.getOptionalIngredients());

        List<Ingredient> requiredIngredients = ingredientSimpleService.getIngredientsByIds(request.getIngredients());
        List<Ingredient> optionalIngredients = ingredientSimpleService.getIngredientsByIds(request.getOptionalIngredients());

        Recipe recipe = recipeRepository.save(createRecipeEntity(user, request));

        saveNecessaryIngredientsOfRecipe(recipe, requiredIngredients);
        saveOptionalIngredientsOfRecipe(recipe, optionalIngredients);

        List<StepResponse> stepResponses = stepService.saveStepsForRecipe(recipe, request.getSteps());

        return RecipeResponse.builder()
                .recipeId(recipe.getId())
                .build();
    }

    @Transactional
    public RecipeResponse updateRecipe(User user, Long recipeId, RecipeUpdateRequest request) {
        Util.validateDuplication(request.getIngredients(), request.getOptionalIngredients());

        List<Ingredient> requiredIngredients = ingredientSimpleService.getIngredientsByIds(request.getIngredients());
        List<Ingredient> optionalIngredients = ingredientSimpleService.getIngredientsByIds(request.getOptionalIngredients());

        Recipe recipe = getRecipeById(recipeId);
        if (!Objects.equals(user.getId(), recipe.getAuthor().getId())) {
            throw new PermissionDeniedException(ErrorCode.ACCESS_DENIED);
        }
        recipe = updateRecipeEntity(recipe, request);

        recipeIngredRepository.deleteByRecipeId(recipe.getId());
        saveNecessaryIngredientsOfRecipe(recipe, requiredIngredients);
        saveOptionalIngredientsOfRecipe(recipe, optionalIngredients);

        // TODO : jpa를 통한 delete query 단건 조회로 발생 추후 성능
        recipe.clearSteps();
        stepService.saveStepsForRecipe(recipe, request.getSteps());

        return RecipeResponse.builder()
                .recipeId(recipe.getId())
                .build();
    }

    // TODO : Recipe fetch 할 때 validation 안되서 임의로 추가.
    @Transactional(readOnly = true)
    public RecipeResponse getRecipeResponseById(Long recipeId) {
        validateRecipeById(recipeId);
        Recipe retrievedRecipe = recipeRepository.findAllElementsById(recipeId).orElseThrow(() -> new NotFoundException(ErrorCode.RECIPE_NOT_FOUND));
        List<RecipeIngred> ingredients = recipeIngredRepository.findByRecipeId(recipeId);
        return RecipeResponse.builder()
                .recipeId(retrievedRecipe.getId())
                .title(retrievedRecipe.getTitle())
                .thumbnail(retrievedRecipe.getThumbnail())
                .description(retrievedRecipe.getDescription())
                .steps(retrievedRecipe.getSteps().stream().map(step -> StepResponse.from(step, step.getPhotos(), step.getVideos())).toList())
                .createdAt(retrievedRecipe.getCreatedAt())
                .updatedAt(retrievedRecipe.getUpdatedAt())
                .user(UserSimpleResponse.from(retrievedRecipe.getAuthor()))
                .ingredients(ingredients.stream().filter(RecipeIngred::getIsNecessary).map(i -> IngredSimpleResponse.from(i.getIngredient())).toList())
                .optionalIngredients(ingredients.stream().filter(i -> !i.getIsNecessary()).map(i -> IngredSimpleResponse.from(i.getIngredient())).toList())
                .build();
    }

    void saveNecessaryIngredientsOfRecipe(Recipe recipe, List<Ingredient> ingredients) {
        List<RecipeIngred> recipeIngredList = ingredients.stream()
                .map(ingredient -> new RecipeIngred(recipe, ingredient, true)).toList();
        recipeIngredRepository.saveAll(recipeIngredList);
    }

    void saveOptionalIngredientsOfRecipe(Recipe recipe, List<Ingredient> ingredients) {
        List<RecipeIngred> recipeIngredList = ingredients.stream()
                .map(ingredient -> new RecipeIngred(recipe, ingredient, false)).toList();
        recipeIngredRepository.saveAll(recipeIngredList);
    }

    Recipe createRecipeEntity(User user, RecipeCreateRequest request) {
        return  Recipe.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .thumbnail(request.getThumbnail())
                .build();
    }

    Recipe updateRecipeEntity(Recipe recipe, RecipeUpdateRequest request) {
        recipe.setTitle(request.getTitle());
        recipe.setDescription(request.getDescription());
        recipe.setThumbnail(request.getThumbnail());
        return  recipe;
    }

    void save(Recipe recipe, List<Ingredient> ingredients) {
        List<RecipeIngred> recipeIngredList = ingredients.stream()
                .map(ingredient -> new RecipeIngred(recipe, ingredient, true)).toList();
        recipeIngredRepository.saveAll(recipeIngredList);
    }

    @Transactional
    void validateCurrentUserIsAuthor(Recipe recipe, User user) {
        if (!Objects.equals(user.getId(), recipe.getAuthor().getId())) throw new PermissionDeniedException(ErrorCode.USER_IS_NOT_AUTHOR);
    }

    @Transactional(readOnly = true)
    Recipe getRecipeById(Long recipeId) {
        return recipeRepository.findById(recipeId).orElseThrow(() ->  new NotFoundException(ErrorCode.RECIPE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    void validateRecipeById(Long recipeId) {
        recipeRepository.findById(recipeId).orElseThrow(() ->  new NotFoundException(ErrorCode.RECIPE_NOT_FOUND));
    }

    //TODO : Refactor : 여러 스텝, 여러 비디오, 사진들 한번에 삭제. 일일히 hibernate가 delete 쿼리 보내고 있음. 개선할 방법은?
    // batch query or 비동기 처리.
    @Transactional
    public void deleteRecipeById(User currentUser, Long recipeId) {
        // TODO : 레시피 영상, 사진 s3에서 삭제
        Recipe retrievedRecipe = getRecipeById(recipeId);
        validateCurrentUserIsAuthor(retrievedRecipe, currentUser);
        recipeRepository.delete(retrievedRecipe);
    }

    @Transactional(readOnly = true)
    public Page<RecipeResponse> getRecipeResponses(User user, Boolean isCookable, Integer month, Pageable pageable) {
        Page<Recipe> recipes = recipeRepository.findRecipes(pageable);
        return recipes.map(RecipeResponse::getMeta);
    }
}
