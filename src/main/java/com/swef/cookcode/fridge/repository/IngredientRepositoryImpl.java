package com.swef.cookcode.fridge.repository;

import static com.swef.cookcode.fridge.domain.QFridgeIngredient.fridgeIngredient;
import static com.swef.cookcode.recipe.domain.QRecipe.recipe;
import static com.swef.cookcode.recipe.domain.QRecipeComment.recipeComment;
import static com.swef.cookcode.recipe.domain.QRecipeIngred.recipeIngred;
import static com.swef.cookcode.recipe.domain.QRecipeLike.recipeLike;
import static com.swef.cookcode.fridge.domain.QFridge.fridge;
import static com.swef.cookcode.fridge.domain.QIngredient.ingredient;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swef.cookcode.fridge.dto.response.IngredSimpleResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IngredientRepositoryImpl implements IngredientCustomRepository{

    private final JPAQueryFactory queryFactory;
    @Override
    public List<IngredSimpleResponse> getNecessaryIngredientsForRecipe(Long userId, Long recipeId) {
        List<IngredSimpleResponse> response = queryFactory.select(
                Projections.constructor(IngredSimpleResponse.class,
                        ingredient.id,
                        ingredient.name,
                        isLackExpression(),
                        ingredient.thumbnail))
                .from(recipeIngred)
                .join(ingredient).on(recipeIngred.ingredient.id.eq(ingredient.id).and(recipeIngred.isNecessary.eq(Boolean.TRUE)))
                .fetchJoin()
                .leftJoin(fridgeIngredient)
                .on(fridgeIngredient.fridge.id.eq(getFridgeIdOfUser(userId))
                        .and(fridgeIngredient.ingred.id.eq(recipeIngred.ingredient.id)))
                .where(recipeIngred.recipe.id.eq(recipeId))
                .fetch();
        return response;
    }

    private BooleanExpression isLackExpression() {
        return new CaseBuilder()
                .when(fridgeIngredient.id.isNull())
                .then(true)
                .otherwise(false);
    }

    // TODO : getFridgeIdOfUser 중복
    private JPAQuery<Long> getFridgeIdOfUser(Long userId) {
        return queryFactory.select(fridge.id).from(fridge).where(fridge.owner.id.eq(userId));
    }

    @Override
    public List<IngredSimpleResponse> getOptionalIngredientsForRecipe(Long userId, Long recipeId) {
        return null;
    }
}
