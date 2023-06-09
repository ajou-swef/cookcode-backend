package com.swef.cookcode.recipe.service;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.swef.cookcode.common.ErrorCode;
import com.swef.cookcode.common.error.exception.InvalidRequestException;
import com.swef.cookcode.common.util.Util;
import com.swef.cookcode.common.dto.CommentCreateRequest;
import com.swef.cookcode.common.dto.CommentResponse;
import com.swef.cookcode.common.error.exception.NotFoundException;
import com.swef.cookcode.common.error.exception.PermissionDeniedException;
import com.swef.cookcode.cookie.repository.CookieRepository;
import com.swef.cookcode.fridge.domain.Ingredient;
import com.swef.cookcode.fridge.service.FridgeService;
import com.swef.cookcode.fridge.service.IngredientSimpleService;
import com.swef.cookcode.recipe.domain.Recipe;
import com.swef.cookcode.recipe.domain.RecipeComment;
import com.swef.cookcode.recipe.domain.RecipeIngred;
import com.swef.cookcode.recipe.domain.RecipeLike;
import com.swef.cookcode.recipe.domain.StepPhoto;
import com.swef.cookcode.recipe.domain.StepVideo;
import com.swef.cookcode.recipe.dto.request.RecipeCreateRequest;
import com.swef.cookcode.recipe.dto.request.RecipeUpdateRequest;
import com.swef.cookcode.recipe.dto.response.RecipeResponse;
import com.swef.cookcode.recipe.repository.RecipeCommentRepository;
import com.swef.cookcode.recipe.repository.RecipeIngredRepository;
import com.swef.cookcode.recipe.repository.RecipeLikeRepository;
import com.swef.cookcode.recipe.repository.RecipeRepository;
import com.swef.cookcode.user.domain.User;
import com.swef.cookcode.user.service.UserSimpleService;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final RecipeCommentRepository recipeCommentRepository;

    private final StepService stepService;
    private final IngredientSimpleService ingredientSimpleService;

    private final UserSimpleService userSimpleService;

    private final RecipeLikeRepository recipeLikeRepository;

    private final CookieRepository cookieRepository;

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

    @Transactional(readOnly = true)
    public RecipeResponse getRecipeResponseById(User user, Long recipeId) {
        return recipeRepository.findRecipeById(user.getId(), recipeId).orElseThrow(() -> new NotFoundException(ErrorCode.RECIPE_NOT_FOUND));
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
        recipeCommentRepository.deleteAllByRecipeId(recipeId);
        recipeLikeRepository.deleteAllByRecipeId(recipeId);
        cookieRepository.updateCookieWhenRecipeDeleted(recipeId);
        recipeRepository.delete(recipe);
    }

    @Transactional(readOnly = true)
    public Slice<RecipeResponse> getRecipeResponses(User user, Boolean isCookable, Integer month, Pageable pageable) {
        if (nonNull(month) && (month < 1 || month > 12)) throw new InvalidRequestException(ErrorCode.INVALID_INPUT_VALUE);
        Slice<RecipeResponse> responses = recipeRepository.findRecipes(user.getId(), isCookable, month, pageable);
        return responses;
    }

    @Transactional(readOnly = true)
    public Slice<RecipeResponse> getRecipeResponsesOfUser(User user, Long targetUserId, Pageable pageable) {
        userSimpleService.checkUserExists(targetUserId);
        Slice<RecipeResponse> responses = recipeRepository.findRecipesOfUser(user.getId(), targetUserId, pageable);
        return responses;
    }

    @Transactional(readOnly = true)
    public Slice<RecipeResponse> searchRecipesWith(User user, String query, Boolean isCookable, Pageable pageable) {
        Slice<RecipeResponse> responses = recipeRepository.searchRecipes(user.getId(), query, isCookable, pageable);
        return responses;
    }

    @Transactional
    public void createComment(User user, Long recipeId, CommentCreateRequest request) {
        Recipe recipe = getRecipeById(recipeId);
        RecipeComment comment = new RecipeComment(recipe, user, request.getComment());
        recipeCommentRepository.save(comment);
    }

    RecipeComment getRecipeCommentById(Long commentId) {
        return recipeCommentRepository.findById(commentId).orElseThrow(() -> new NotFoundException(ErrorCode.RECIPE_COMMENT_NOT_FOUND));
    }

    @Transactional
    public void deleteCommentOfRecipe(User user, Long commentId) {
        RecipeComment comment = getRecipeCommentById(commentId);
        if (!Objects.equals(user.getId(), comment.getUser().getId())) throw new PermissionDeniedException(ErrorCode.RECIPE_COMMENT_USER_MISSMATCH);
        recipeCommentRepository.delete(comment);
    }

    public Slice<CommentResponse> getCommentsOfRecipe(Long recipeId, Pageable pageable) {
        if (recipeRepository.findById(recipeId).isEmpty()) throw new NotFoundException(ErrorCode.RECIPE_NOT_FOUND);
        return recipeCommentRepository.findRecipeComments(recipeId, pageable).map(CommentResponse::from);
    }

    @Transactional
    public void toggleRecipeLike(User user, Long recipeId) {
        Optional<RecipeLike> likeOptional = recipeLikeRepository.findByUserIdAndRecipeId(user.getId(), recipeId);
        likeOptional.ifPresentOrElse(this::unlikeRecipe, () -> likeRecipe(user, recipeId));
    }

    void likeRecipe(User user, Long recipeId) {
        Recipe recipe = getRecipeById(recipeId);
        recipeLikeRepository.save(new RecipeLike(recipe, user));
    }

    void unlikeRecipe(RecipeLike recipeLike) {
        recipeLikeRepository.delete(recipeLike);
    }


}
