package com.swef.cookcode.recipe.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Builder
@AllArgsConstructor
public class RecipeCreateRequest {

    @NotBlank
    protected String title;

    @NotBlank
    protected String description;

    @Size(min = 1)
    protected List<Long> ingredients;

    protected List<Long> optionalIngredients;

    @Size(min = 1)
    protected List<StepCreateRequest> steps;

    @NotBlank
    protected String thumbnail;

    protected List<String> deletedThumbnails;
}
