package com.swef.cookcode.recipe.service;

import com.swef.cookcode.common.ErrorCode;
import com.swef.cookcode.common.Util;
import com.swef.cookcode.common.error.exception.NotFoundException;
import com.swef.cookcode.common.error.exception.PermissionDeniedException;
import com.swef.cookcode.fridge.domain.Ingredient;
import com.swef.cookcode.fridge.dto.IngredientSimpleResponse;
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
import com.swef.cookcode.user.service.UserSimpleService;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

        //Recipe의 Steps 생성
        List<StepResponse> stepResponses = stepService.saveStepsForRecipe(savedRecipe, request.getSteps());

        return RecipeResponse.builder()
                .recipeId(savedRecipe.getId())
                .build();
    }

    @Transactional
    public RecipeResponse updateRecipe(User currentUser, Long recipeId, RecipeUpdateRequest request) {
        //Ingredient
        Util.validateDuplication(request.getIngredients(), request.getOptionalIngredients());

        List<Ingredient> requiredIngredients = ingredientSimpleService.getIngredientsByIds(request.getIngredients());
        List<Ingredient> optionalIngredients = ingredientSimpleService.getIngredientsByIds(request.getOptionalIngredients());

        //Recipe 조회
        Recipe retrivedRecipe = getRecipeById(recipeId);
        retrivedRecipe.setTitle(request.getTitle());
        retrivedRecipe.setDescription(request.getDescription());
        retrivedRecipe.setThumbnail(request.getThumbnail());

        //Recipe의 Ingredients 수정
        recipeIngredRepository.deleteByRecipeId(retrivedRecipe.getId());
        saveIngredientsOfRecipe(retrivedRecipe, requiredIngredients, true);
        saveIngredientsOfRecipe(retrivedRecipe, optionalIngredients, false);

        //Recipe의 Steps 수정
        // TODO : jpa를 통한 delete query 단건 조회로 발생 추후 성능
        retrivedRecipe.clearSteps();
        stepService.saveStepsForRecipe(retrivedRecipe, request.getSteps());

        return RecipeResponse.builder()
                .recipeId(retrivedRecipe.getId())
                .build();
    }

    // TODO : recipe repository에서 ingredients까지 한번에 가져올 방법 없을지
    // TODO : Recipe fetch 할 때 validation 안되서 임의로 추가.
    @Transactional(readOnly = true)
    public RecipeResponse getRecipeResponseById(Long recipeId) {
        validateRecipeById(recipeId);
        Recipe retrievedRecipe = recipeRepository.findAllElementsById(recipeId).orElseThrow(() -> new NotFoundException(ErrorCode.RECIPE_NOT_FOUND));
        List<Ingredient> ingredients = recipeIngredRepository.findByRecipeIdAndIsNecessary(recipeId, true);
        List<Ingredient> optionalIngredient = recipeIngredRepository.findByRecipeIdAndIsNecessary(recipeId, false);
        return RecipeResponse.builder()
                .recipeId(retrievedRecipe.getId())
                .title(retrievedRecipe.getTitle())
                .thumbnail(retrievedRecipe.getThumbnail())
                .description(retrievedRecipe.getDescription())
                .steps(retrievedRecipe.getSteps().stream().map(step -> StepResponse.from(step, step.getPhotos(), step.getVideos())).toList())
                .createdAt(retrievedRecipe.getCreatedAt())
                .updatedAt(retrievedRecipe.getUpdatedAt())
                .user(UserSimpleResponse.from(retrievedRecipe.getAuthor()))
                .ingredients(ingredients.stream().map(IngredientSimpleResponse::from).toList())
                .optionalIngredients(optionalIngredient.stream().map(IngredientSimpleResponse::from).toList())
                .build();
    }

    @Transactional
    void saveIngredientsOfRecipe(Recipe recipe, List<Ingredient> ingredients, Boolean isNecessary) {
        List<RecipeIngred> recipeIngredList = ingredients.stream()
                .map(ingredient -> new RecipeIngred(recipe, ingredient, isNecessary)).toList();
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
        Recipe retrivedRecipe = getRecipeById(recipeId);
        validateCurrentUserIsAuthor(retrivedRecipe, currentUser);
        recipeRepository.delete(retrivedRecipe);
    }

}
