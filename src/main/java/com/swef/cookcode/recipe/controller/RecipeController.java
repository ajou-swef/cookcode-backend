package com.swef.cookcode.recipe.controller;

import com.swef.cookcode.common.ApiResponse;
import com.swef.cookcode.common.entity.CurrentUser;
import com.swef.cookcode.fridge.dto.IngredientSimpleResponse;
import com.swef.cookcode.recipe.domain.Recipe;
import com.swef.cookcode.recipe.dto.request.RecipeCreateRequest;
import com.swef.cookcode.recipe.dto.response.RecipeResponse;
import com.swef.cookcode.recipe.dto.response.StepResponse;
import com.swef.cookcode.recipe.service.RecipeService;
import com.swef.cookcode.user.domain.User;
import com.swef.cookcode.user.dto.response.UserSimpleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/recipe")
@RequiredArgsConstructor
@SuppressWarnings({"rawtypes", "unchecked"})
public class RecipeController {

    private final RecipeService recipeService;

    @PostMapping
    public ResponseEntity<ApiResponse<RecipeResponse>> createRecipe(@CurrentUser User user, @RequestBody RecipeCreateRequest recipeCreateRequest){
        // 레시피 서비스에서 레시피, 스텝 생성
        // s3에 삭제
        ApiResponse apiResponse = ApiResponse.builder()
                .message("레시피 생성 성공")
                .status(HttpStatus.CREATED.value())
                .data(recipeService.createRecipe(user, recipeCreateRequest))
                .build();

        return ResponseEntity.ok()
                .body(apiResponse);
    }

    @GetMapping("/{recipeId}")
    public ResponseEntity<ApiResponse<RecipeResponse>> getRecipeById(@CurrentUser User user, @PathVariable("recipeId") Long recipeId) {

        ApiResponse apiResponse = ApiResponse.builder()
                .message("레시피 세부 조회 성공")
                .status(HttpStatus.CREATED.value())
                .data(recipeService.getRecipeById(recipeId))
                .build();

        return ResponseEntity.ok()
                .body(apiResponse);
    }

}
