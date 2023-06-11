package com.swef.cookcode.recipe.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.swef.cookcode.common.util.Util;
import com.swef.cookcode.cookie.repository.CookieRepository;
import com.swef.cookcode.fridge.domain.Category;
import com.swef.cookcode.fridge.domain.Ingredient;
import com.swef.cookcode.fridge.service.IngredientSimpleService;
import com.swef.cookcode.recipe.domain.Recipe;
import com.swef.cookcode.recipe.dto.request.RecipeCreateRequest;
import com.swef.cookcode.recipe.dto.request.StepCreateRequest;
import com.swef.cookcode.recipe.repository.RecipeCommentRepository;
import com.swef.cookcode.recipe.repository.RecipeIngredRepository;
import com.swef.cookcode.recipe.repository.RecipeLikeRepository;
import com.swef.cookcode.recipe.repository.RecipeRepository;
import com.swef.cookcode.user.domain.Authority;
import com.swef.cookcode.user.domain.User;
import com.swef.cookcode.user.service.UserSimpleService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
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

    private Util util;
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

                doReturn(ingredients).when(ingredientSimpleService).getIngredientsByIds(createRequest.getIngredients());
                doReturn(optionalIngredients).when(ingredientSimpleService).getIngredientsByIds(createRequest.getOptionalIngredients());
                doReturn(recipe).when(recipeRepository).save(any());

                //when
                recipeService.createRecipe(user, createRequest);

                //then
                verify(recipeRepository).save(any());
                verify(recipeIngredRepository, times(2)).saveAll(any());
                verify(stepService).saveStepsForRecipe(recipe, createRequest.getSteps());
            }
        }

        @Nested
        @DisplayName("레시피 생성 실패")
        class Failure {
            @Test
            @DisplayName("레시피 스텝 파일이 없는 경우")
            void successWhenStepFilesExist() {
                StepCreateRequest stepCreateRequest = StepCreateRequest.builder()
                        .deletedPhotos(List.of("deletedPhoto"))
                        .deletedVideos(List.of("deletedVideo"))
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

                List<Ingredient> ingredients = List.of(ingredient);
                List<Ingredient> optionalIngredients = List.of(optionalIngredient);

                doReturn(ingredients).when(ingredientSimpleService).getIngredientsByIds(createRequest.getIngredients());
                doReturn(optionalIngredients).when(ingredientSimpleService).getIngredientsByIds(createRequest.getOptionalIngredients());
                doReturn(recipe).when(recipeRepository).save(any());

                //when
                recipeService.createRecipe(user, createRequest);

                //then
                verify(recipeRepository).save(any());
                verify(recipeIngredRepository, times(2)).saveAll(any());
                verify(stepService).saveStepsForRecipe(recipe, createRequest.getSteps());
            }

        }


    }
}