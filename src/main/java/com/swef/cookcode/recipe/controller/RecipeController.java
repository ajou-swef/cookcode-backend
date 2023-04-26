package com.swef.cookcode.recipe.controller;

import com.swef.cookcode.recipe.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/recipe")
@RequiredArgsConstructor
@SuppressWarnings({"rawtypes", "unchecked"})
public class RecipeController {

    private final RecipeService recipeService;



}
