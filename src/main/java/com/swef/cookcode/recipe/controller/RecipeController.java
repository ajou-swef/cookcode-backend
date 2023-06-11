package com.swef.cookcode.recipe.controller;

import com.swef.cookcode.common.ApiResponse;
import com.swef.cookcode.common.SliceResponse;
import com.swef.cookcode.common.dto.UrlResponse;
import com.swef.cookcode.common.util.Util;
import com.swef.cookcode.common.dto.CommentCreateRequest;
import com.swef.cookcode.common.dto.CommentResponse;
import com.swef.cookcode.common.entity.CurrentUser;
import com.swef.cookcode.recipe.dto.request.RecipeCreateRequest;
import com.swef.cookcode.recipe.dto.request.RecipeUpdateRequest;
import com.swef.cookcode.recipe.dto.response.RecipeResponse;
import com.swef.cookcode.recipe.dto.response.UpdateResponse;
import com.swef.cookcode.recipe.service.RecipeService;
import com.swef.cookcode.user.domain.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1/recipe")
@RequiredArgsConstructor
@SuppressWarnings({"rawtypes", "unchecked"})
public class RecipeController {

    private final RecipeService recipeService;

    private final Util util;

    @PostMapping
    public ResponseEntity<ApiResponse<UpdateResponse>> createRecipe(@CurrentUser User user, @RequestBody RecipeCreateRequest recipeCreateRequest){

        recipeService.deleteCancelledFiles(recipeCreateRequest);
        UpdateResponse response = recipeService.createRecipe(user, recipeCreateRequest);

        ApiResponse apiResponse = ApiResponse.builder()
                .message("레시피 생성 성공")
                .status(HttpStatus.CREATED.value())
                .data(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<SliceResponse<RecipeResponse>>> searchRecipes(@CurrentUser User user, @RequestParam(value = "query") String query,
                                                                                   @RequestParam(value = "cookable", required = false) Boolean isCookable,
                                                                                   @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {


        SliceResponse<RecipeResponse> response = new SliceResponse<>(recipeService.searchRecipesWith(user, query, isCookable, pageable));
        ApiResponse apiResponse = ApiResponse.builder()
                .message("레시피 검색 성공")
                .status(HttpStatus.OK.value())
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/{recipeId}/comments")
    public ResponseEntity<ApiResponse> commentRecipe(@CurrentUser User user, @PathVariable(value = "recipeId") Long recipeId, @RequestBody
    CommentCreateRequest request) {
        recipeService.createComment(user, recipeId, request);
        ApiResponse apiResponse = ApiResponse.builder()
                .message("레시피 댓글 작성 성공")
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse> deleteComment(@CurrentUser User user, @PathVariable(value = "commentId") Long commentId) {
        recipeService.deleteCommentOfRecipe(user, commentId);
        ApiResponse apiResponse = ApiResponse.builder()
                .message("레시피 댓글 삭제 성공")
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponse);

    }

    @GetMapping("/{recipeId}/comments")
    public ResponseEntity<ApiResponse<SliceResponse<CommentResponse>>> getCommentsOfRecipe(@PathVariable(value = "recipeId") Long recipeId,
                                                                                           @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
                                                                                                 Pageable pageable) {
        SliceResponse<CommentResponse> response = new SliceResponse<>(recipeService.getCommentsOfRecipe(recipeId, pageable));
        ApiResponse apiResponse = ApiResponse.builder()
                .message("레시피 댓글 다건 조회 성공")
                .status(HttpStatus.OK.value())
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);

    }

    @PostMapping("/{recipeId}/likes")
    public ResponseEntity<ApiResponse> likeRecipe(@CurrentUser User user, @PathVariable(value = "recipeId") Long recipeId) {
        recipeService.toggleRecipeLike(user, recipeId);
        ApiResponse apiResponse = ApiResponse.builder()
                .message("레시피 좋아요 성공")
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok().body(apiResponse);
    }


    @PostMapping("/files/{directory}")
    public ResponseEntity<ApiResponse<UrlResponse>> uploadRecipePhotos(@RequestPart(value = "stepFiles") List<MultipartFile> files, @PathVariable(value = "directory") String directory) {
        UrlResponse response = util.uploadFilesToS3(directory, files);
        ApiResponse apiResponse = ApiResponse.builder()
                .message("파일 업로드 성공")
                .status(HttpStatus.OK.value())
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/{recipeId}")
    public ResponseEntity<ApiResponse<UpdateResponse>> updateRecipe(@CurrentUser User user, @PathVariable("recipeId") Long recipeId, @RequestBody RecipeUpdateRequest recipeUpdateRequest){

        recipeService.deleteCancelledFiles(recipeUpdateRequest);
        UpdateResponse response = recipeService.updateRecipe(user, recipeId, recipeUpdateRequest);

        ApiResponse apiResponse = ApiResponse.builder()
                .message("레시피 수정 성공")
                .status(HttpStatus.OK.value())
                .data(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<SliceResponse<RecipeResponse>>> getRecipe(@CurrentUser User user,
                                                                               @RequestParam(value = "cookable", required = false) Boolean isCookable,
                                                                               @RequestParam(value = "month", required = false) Integer month,
                                                                               @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
                                                                               Pageable pageable)
    {
        SliceResponse<RecipeResponse> response = new SliceResponse<>(recipeService.getRecipeResponses(user, isCookable, month, pageable));
        ApiResponse apiResponse = ApiResponse.builder()
                .message("레시피 다건 조회 성공")
                .status(HttpStatus.OK.value())
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/user/{targetUserId}")
    public ResponseEntity<ApiResponse<SliceResponse<RecipeResponse>>> getRecipesOfUser(@CurrentUser User user, @PathVariable(value = "targetUserId") Long userId,
                                                                                       @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
                                                                                       Pageable pageable) {
        SliceResponse<RecipeResponse> response = new SliceResponse<>(recipeService.getRecipeResponsesOfUser(user, userId, pageable));
        ApiResponse apiResponse = ApiResponse.builder()
                .message("유저의 레시피 조회 성공")
                .status(HttpStatus.OK.value())
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{recipeId}")
    public ResponseEntity<ApiResponse<RecipeResponse>> getRecipeById(@CurrentUser User user, @PathVariable("recipeId") Long recipeId) {

        ApiResponse apiResponse = ApiResponse.builder()
                .message("레시피 세부 조회 성공")
                .status(HttpStatus.OK.value())
                .data(recipeService.getRecipeResponseById(user, recipeId))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{recipeId}")
    public ResponseEntity<ApiResponse> deleteRecipeById(@CurrentUser User user, @PathVariable("recipeId") Long recipeId) {


        recipeService.deleteRecipeById(user, recipeId);

        ApiResponse apiResponse = ApiResponse.builder()
                .message("레시피 삭제 성공")
                .status(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

}
