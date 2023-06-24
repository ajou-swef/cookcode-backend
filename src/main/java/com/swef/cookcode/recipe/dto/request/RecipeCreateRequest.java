package com.swef.cookcode.recipe.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Getter
@SuperBuilder
@AllArgsConstructor
public class RecipeCreateRequest {

    @NotBlank
    protected String title;

    @NotBlank
    protected String description;

    @NotNull
    @Size(min = 1)
    protected List<Long> ingredients;

    protected List<Long> optionalIngredients;

    @NotNull
    @Size(min = 1)
    protected List<StepCreateRequest> steps;

    @NotBlank
    protected String thumbnail;

    protected List<String> deletedThumbnails;

    protected Boolean isPremium;
}
