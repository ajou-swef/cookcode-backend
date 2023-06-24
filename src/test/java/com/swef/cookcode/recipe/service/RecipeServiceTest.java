package com.swef.cookcode.recipe.service;

import static com.swef.cookcode.common.ErrorCode.RECIPE_NOT_FOUND;
import static com.swef.cookcode.common.ErrorCode.STEP_FILES_NECESSARY;
import static com.swef.cookcode.common.ErrorCode.USER_IS_NOT_AUTHOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.swef.cookcode.common.ErrorCode;
import com.swef.cookcode.common.error.exception.InvalidRequestException;
import com.swef.cookcode.common.error.exception.NotFoundException;
import com.swef.cookcode.common.error.exception.PermissionDeniedException;
import com.swef.cookcode.common.util.Util;
import com.swef.cookcode.cookie.repository.CookieRepository;
import com.swef.cookcode.fridge.domain.Category;
import com.swef.cookcode.fridge.domain.Ingredient;
import com.swef.cookcode.fridge.service.IngredientSimpleService;
import com.swef.cookcode.recipe.domain.Recipe;
import com.swef.cookcode.recipe.dto.request.RecipeCreateRequest;
import com.swef.cookcode.recipe.dto.request.RecipeUpdateRequest;
import com.swef.cookcode.recipe.dto.request.StepCreateRequest;
import com.swef.cookcode.recipe.repository.RecipeCommentRepository;
import com.swef.cookcode.recipe.repository.RecipeIngredRepository;
import com.swef.cookcode.recipe.repository.RecipeLikeRepository;
import com.swef.cookcode.recipe.repository.RecipeRepository;
import com.swef.cookcode.user.domain.Authority;
import com.swef.cookcode.user.domain.User;
import com.swef.cookcode.user.service.UserSimpleService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.spel.ast.OpInc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;


@DisplayName("레시피 행위 테스트")
@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private RecipeIngredRepository recipeIngredRepository;

    @Mock
    private RecipeCommentRepository recipeCommentRepository;

    @Mock
    private StepService stepService;

    @Mock
    private IngredientSimpleService ingredientSimpleService;

    @Mock
    private UserSimpleService userSimpleService;

    @Mock
    private RecipeLikeRepository recipeLikeRepository;

    @Mock
    private CookieRepository cookieRepository;
    @Spy
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @InjectMocks
    private RecipeService recipeService;

    private Ingredient ingredient = Ingredient.builder()
            .name("삼겹살")
            .category(Category.MEAT)
            .build();
    private Ingredient optionalIngredient = Ingredient.builder()
            .name("목살")
            .category(Category.MEAT)
            .build();

    private User user = User.builder()
            .email("julie0005@ajou.ac.kr")
            .nickname("테스트유저")
            .authority(Authority.USER)
            .encodedPassword(passwordEncoder.encode("test1234"))
            .build();

    private Recipe recipe = Recipe.builder()
            .user(user)
            .title("레시피 제목입니다.")
            .description("레시피 설명입니다.")
            .thumbnail("thumbnailUrl")
            .build();

    private List<MultipartFile> photoFiles = List.of(
            new MockMultipartFile(
                    "test1",
                    "test1.png",
                    MediaType.MULTIPART_FORM_DATA_VALUE,
                    "test1".getBytes()),
            new MockMultipartFile(
                    "test2",
                    "test2.jpeg",
                    MediaType.MULTIPART_FORM_DATA_VALUE,
                    "test2".getBytes())
    );

    private List<MultipartFile> videoFiles = List.of(
            new MockMultipartFile(
                    "test1",
                    "test1.mp4",
                    MediaType.MULTIPART_FORM_DATA_VALUE,
                    "test1".getBytes()),
            new MockMultipartFile(
                    "test2",
                    "test2.mov",
                    MediaType.MULTIPART_FORM_DATA_VALUE,
                    "test2".getBytes())
    );

    @DisplayName("레시피 생성할 때")
    @Nested
    class RecipeCreationTest{
        StepCreateRequest stepCreateRequest = StepCreateRequest.builder()
                .deletedPhotos(List.of("deletedPhoto"))
                .deletedVideos(List.of("deletedVideo"))
                .photos(List.of("photos"))
                .videos(List.of("videos"))
                .seq(1L)
                .title("스텝 제목")
                .description("스텝 설명").build();

        RecipeCreateRequest createRequest = RecipeCreateRequest.builder()
                .title(recipe.getTitle())
                .description(recipe.getDescription())
                .ingredients(List.of(1L))
                .optionalIngredients(List.of(2L))
                .deletedThumbnails(List.of("deletedThumbnail"))
                .steps(List.of(stepCreateRequest))
                .thumbnail(recipe.getThumbnail())
                .build();

        @Nested
        @DisplayName("성공하는 경우")
        class Success {

            @Test
            @DisplayName("레시피 스텝 파일이 있을 때")
            void successWhenStepFilesExist() {
                List<Ingredient> ingredients = List.of(ingredient);
                List<Ingredient> optionalIngredients = List.of(optionalIngredient);
                given(ingredientSimpleService.getIngredientsByIds(createRequest.getIngredients())).willReturn(ingredients);
                given(ingredientSimpleService.getIngredientsByIds(createRequest.getOptionalIngredients())).willReturn(optionalIngredients);
                given(recipeRepository.save(any())).willReturn(recipe);

                //when
                recipeService.createRecipe(user, createRequest);

                //then
                verify(recipeRepository).save(any());
                verify(recipeIngredRepository, times(2)).saveAll(any());
                verify(stepService, only()).saveStepsForRecipe(recipe, createRequest.getSteps());
            }
        }

        @Nested
        @DisplayName("실패하는 경우")
        class Failure {
            @Test
            @DisplayName("레시피 스텝 파일이 없는 경우")
            void failWhenStepFilesEmpty() {
                StepCreateRequest stepCreateRequest = StepCreateRequest.builder()
                        .deletedPhotos(List.of("deletedPhoto"))
                        .deletedVideos(List.of("deletedVideo"))
                        .photos(Collections.emptyList())
                        .videos(Collections.emptyList())
                        .seq(1L)
                        .title("스텝 제목")
                        .description("스텝 설명").build();
                RecipeCreateRequest createRequest = RecipeCreateRequest.builder()
                        .title(recipe.getTitle())
                        .description(recipe.getDescription())
                        .ingredients(List.of(1L))
                        .optionalIngredients(List.of(2L))
                        .deletedThumbnails(List.of("deletedThumbnail"))
                        .steps(List.of(stepCreateRequest))
                        .thumbnail(recipe.getThumbnail())
                        .build();

                given(stepService.saveStepsForRecipe(recipe, createRequest.getSteps())).willCallRealMethod();

                assertThatThrownBy(() -> {stepService.saveStepsForRecipe(recipe, createRequest.getSteps());})
                        .isInstanceOf(InvalidRequestException.class)
                        .hasMessageContaining(STEP_FILES_NECESSARY.getMessage());
            }

            @Test
            @DisplayName("필수 재료 선택 재료 중복인 경우")
            void failWhenIngredientsDuplicated() {
                //given
                List<Long> ingredientIds = List.of(1L, 2L, 3L);
                List<Long> optionalIngredientIds = List.of(4L, 5L, 1L, 6L);
                RecipeCreateRequest duplicatedRequest = RecipeCreateRequest.builder()
                        .title(recipe.getTitle())
                        .description(recipe.getDescription())
                        .ingredients(ingredientIds)
                        .optionalIngredients(optionalIngredientIds)
                        .deletedThumbnails(List.of("deletedThumbnail"))
                        .steps(List.of(stepCreateRequest))
                        .thumbnail(recipe.getThumbnail())
                        .build();

                //then
                assertThatThrownBy(() -> recipeService.createRecipe(user, duplicatedRequest))
                        .isInstanceOf(InvalidRequestException.class)
                        .hasMessageContaining(ErrorCode.DUPLICATED.getMessage());
            }

        }
    }

    @DisplayName("레시피 수정할 때")
    @Nested
    class RecipeUpdateTest {
        StepCreateRequest stepUpdateRequest = StepCreateRequest.builder()
                .deletedPhotos(List.of("deletedPhoto"))
                .deletedVideos(List.of("deletedVideo"))
                .photos(List.of("photos"))
                .videos(List.of("videos"))
                .seq(1L)
                .title("스텝 제목")
                .description("스텝 설명").build();

        RecipeUpdateRequest updateRequest = RecipeUpdateRequest.builder()
                .title("수정 제목")
                .description("수정 설명")
                .ingredients(List.of(1L))
                .optionalIngredients(List.of(2L))
                .deletedThumbnails(List.of("deletedThumbnail"))
                .steps(List.of(stepUpdateRequest))
                .thumbnail("수정 썸네일")
                .build();

        User author = Mockito.mock(User.class);
        Recipe recipeWithMockAuthor = Recipe.builder()
                .user(author)
                .title("레시피 제목입니다.")
                .description("레시피 설명입니다.")
                .thumbnail("thumbnailUrl")
                .build();
        @Nested
        @DisplayName("실패하는 경우")
        class Failure{

            @Test
            @DisplayName("존재하지 않는 레시피를 수정하려는 경우")
            void failWhenNonExist() {
                // given
                given(recipeRepository.findById(1L)).willReturn(Optional.empty());

                // when then
                assertThatThrownBy(() -> recipeService.updateRecipe(user, 1L, updateRequest)).isInstanceOf(
                        NotFoundException.class)
                        .hasMessageContaining(RECIPE_NOT_FOUND.getMessage());
            }
            @Test
            @DisplayName("레시피 수정하려는 사람이 글쓴이가 아닌 경우")
            void failWhenAuthorIsNotUser() {
                // given
                User anotherUser = Mockito.mock(User.class);
                given(anotherUser.getId()).willReturn(1L);
                given(author.getId()).willReturn(2L);
                given(recipeRepository.findById(1L)).willReturn(Optional.of(recipeWithMockAuthor));

                assertThatThrownBy(() -> recipeService.updateRecipe(anotherUser, 1L, updateRequest))
                        .isInstanceOf(PermissionDeniedException.class)
                        .hasMessageContaining(USER_IS_NOT_AUTHOR.getMessage());
            }

            @Test
            @DisplayName("필수 재료 선택 재료 중복인 경우")
            void failWhenIngredientsDuplicated() {
                //given
                List<Long> ingredientIds = List.of(1L, 2L, 3L);
                List<Long> optionalIngredientIds = List.of(4L, 5L, 1L, 6L);
                RecipeUpdateRequest duplicatedRequest = RecipeUpdateRequest.builder()
                        .title(recipe.getTitle())
                        .description(recipe.getDescription())
                        .ingredients(ingredientIds)
                        .optionalIngredients(optionalIngredientIds)
                        .deletedThumbnails(List.of("deletedThumbnail"))
                        .steps(List.of(stepUpdateRequest))
                        .thumbnail(recipe.getThumbnail())
                        .build();

                //when then
                assertThatThrownBy(() -> recipeService.updateRecipe(user, 1L, duplicatedRequest))
                        .isInstanceOf(InvalidRequestException.class)
                        .hasMessageContaining(ErrorCode.DUPLICATED.getMessage());
            }
        }

        @Nested
        @DisplayName("성공하는 경우")
        class Success{
            @Test
            @DisplayName("정상적으로 수정됨")
            void success() {
                //given
                given(author.getId()).willReturn(1L);
                List<Ingredient> ingredients = List.of(ingredient);
                List<Ingredient> optionalIngredients = List.of(optionalIngredient);
                given(ingredientSimpleService.getIngredientsByIds(updateRequest.getIngredients())).willReturn(ingredients);
                given(ingredientSimpleService.getIngredientsByIds(updateRequest.getOptionalIngredients())).willReturn(optionalIngredients);
                given(recipeRepository.findById(1L)).willReturn(Optional.of(recipeWithMockAuthor));

                //when
                recipeService.updateRecipe(author, 1L, updateRequest);

                //then
                assertThat(recipeWithMockAuthor.getTitle()).isEqualTo(updateRequest.getTitle());
                assertThat(recipeWithMockAuthor.getDescription()).isEqualTo(updateRequest.getDescription());
                assertThat(recipeWithMockAuthor.getThumbnail()).isEqualTo(updateRequest.getThumbnail());

                verify(recipeIngredRepository).deleteByRecipeId(recipeWithMockAuthor.getId());
                verify(recipeIngredRepository, times(2)).saveAll(any());
                verify(stepService, only()).saveStepsForRecipe(recipeWithMockAuthor, updateRequest.getSteps());
            }
        }
    }

    @Nested
    @DisplayName("레시피 삭제할 때")
    class RecipeDeletionTest{
        @Nested
        @DisplayName("실패하는 경우")
        class Failure{
            @Test
            @DisplayName("존재하지 않는 레시피를 삭제하려는 경우")
            void failWhenNonExist() {

            }
            @Test
            @DisplayName("레시피 삭제하려는 사람이 글쓴이가 아닌 경우")
            void failWhenAuthorIsNotUser() {

            }
        }

        @Nested
        @DisplayName("성공하는 경우")
        class Success{
            @Test
            @DisplayName("정상적으로 삭제됨")
            void success() {

            }
        }
    }

    @Nested
    @DisplayName("레시피 조회할 때")
    class RecipeReadTest{
        @Test
        @DisplayName("레시피 상세 조회 성공")
        void testGetDetailRecipe() {

        }

        @Test
        @DisplayName("레시피 상세 조회 실패 : 존재하지 않는 레시피")
        void failWhenNonExistRecipe() {

        }

        @Test
        @DisplayName("레시피 다건 조회 성공")
        void testGetRecipeLists() {

        }

    }
}