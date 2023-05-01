package com.swef.cookcode.recipe.service;

import static org.junit.jupiter.api.Assertions.*;

import com.swef.cookcode.fridge.domain.Ingredient;
import com.swef.cookcode.recipe.domain.Recipe;
import com.swef.cookcode.recipe.repository.RecipeRepository;
import com.swef.cookcode.user.domain.Authority;
import com.swef.cookcode.user.domain.User;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Spy
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @InjectMocks
    private RecipeService recipeService;

    private Ingredient ingredient = Ingredient.builder()
            .name("삼겹살")
            .category("육류")
            .build();
    private Ingredient optionalIngredient = Ingredient.builder()
            .name("목살")
            .category("육류")
            .build();

    private User user = User.builder()
            .email("julie0005@ajou.ac.kr")
            .nickname("테스트유저")
            .authority(Authority.USER)
            .encodedPassword(passwordEncoder.encode("test1234"))
            .build();

    @BeforeEach
    void setUp() {


    }

//    @Test
//    void testGetRecipe() {
//        recipeService.getRecipeById()
//    }
}