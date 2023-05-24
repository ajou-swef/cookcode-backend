package com.swef.cookcode.recipe.service;

import static java.util.Objects.isNull;

import com.swef.cookcode.common.ErrorCode;
import com.swef.cookcode.common.Util;
import com.swef.cookcode.common.error.exception.NotFoundException;
import com.swef.cookcode.common.error.exception.PermissionDeniedException;
import com.swef.cookcode.fridge.domain.Ingredient;
import com.swef.cookcode.fridge.service.FridgeService;
import com.swef.cookcode.fridge.service.IngredientSimpleService;
import com.swef.cookcode.recipe.domain.Recipe;
import com.swef.cookcode.recipe.domain.RecipeIngred;
import com.swef.cookcode.recipe.domain.StepPhoto;
import com.swef.cookcode.recipe.domain.StepVideo;
import com.swef.cookcode.recipe.dto.request.RecipeCreateRequest;
import com.swef.cookcode.recipe.dto.request.RecipeUpdateRequest;
import com.swef.cookcode.recipe.dto.response.RecipeResponse;
import com.swef.cookcode.recipe.repository.RecipeIngredRepository;
import com.swef.cookcode.recipe.repository.RecipeRepository;
import com.swef.cookcode.user.domain.User;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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

    private final FridgeService fridgeService;

    private final Util util;

    @Transactional
    public RecipeResponse createRecipe(User user, RecipeCreateRequest request) {
        Util.validateDuplication(request.getIngredients(), request.getOptionalIngredients());

        List<Ingredient> requiredIngredients = ingredientSimpleService.getIngredientsByIds(request.getIngredients());
        List<Ingredient> optionalIngredients = ingredientSimpleService.getIngredientsByIds(request.getOptionalIngredients());

        Recipe recipe = recipeRepository.save(Recipe.createEntity(user, request));

        saveNecessaryIngredientsOfRecipe(recipe, requiredIngredients);
        saveOptionalIngredientsOfRecipe(recipe, optionalIngredients);

        stepService.saveStepsForRecipe(recipe, request.getSteps());

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
        recipe = Recipe.updateEntity(recipe, request);

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

    public void deleteCancelledFiles(RecipeCreateRequest request) {
        util.deleteFilesInS3(request.getDeletedThumbnails());
        request.getSteps().forEach(step -> {
            util.deleteFilesInS3(step.getDeletedPhotos());
            util.deleteFilesInS3(step.getDeletedVideos());
        });
    }

    // TODO : Recipe fetch 할 때 validation 안되서 임의로 추가.
    @Transactional(readOnly = true)
    public RecipeResponse getRecipeResponseById(Long recipeId) {
        validateRecipeById(recipeId);
        Recipe recipe = recipeRepository.findAllElementsById(recipeId).orElseThrow(() -> new NotFoundException(ErrorCode.RECIPE_NOT_FOUND));
        return RecipeResponse.from(recipe);
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
    public Recipe getRecipeOrNull(Long recipeId) {
        return isNull(recipeId) ? null : getRecipeById(recipeId);
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
        Recipe recipe = getRecipeById(recipeId);
        validateCurrentUserIsAuthor(recipe, currentUser);
        util.deleteFilesInS3(List.of(recipe.getThumbnail()));
        recipe.getSteps().forEach(step -> {
            List<String> deletedPhotos = step.getPhotos().stream().map(StepPhoto::getPhotoUrl).toList();
            List<String> deletedVideos = step.getVideos().stream().map(StepVideo::getVideoUrl).toList();
            util.deleteFilesInS3(deletedPhotos);
            util.deleteFilesInS3(deletedVideos);
        });
        recipeRepository.delete(recipe);
    }

    @Transactional(readOnly = true)
    public Page<RecipeResponse> getRecipeResponses(User user, Boolean isCookable, Integer month, Pageable pageable) {
        Long fridgeId = fridgeService.getFridgeOfUser(user).getId();
        Page<RecipeResponse> responses = recipeRepository.findRecipes(fridgeId, isCookable, pageable);
        return responses;
    }

    public Slice<RecipeResponse> getRecipeResponsesBySearch(User user, String query, Boolean isCookable, Pageable pageable) {
        Long fridgeId = fridgeService.getFridgeOfUser(user).getId();
        Slice<RecipeResponse> responses = recipeRepository.searchRecipes(user.getId(), query, isCookable, pageable);
        return responses;
    }
}
