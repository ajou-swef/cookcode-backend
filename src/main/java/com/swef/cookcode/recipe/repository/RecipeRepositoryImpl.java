package com.swef.cookcode.recipe.repository;


import static java.util.Objects.nonNull;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swef.cookcode.fridge.domain.QFridgeIngredient;
import com.swef.cookcode.recipe.domain.QRecipe;
import com.swef.cookcode.recipe.domain.QRecipeIngred;
import com.swef.cookcode.recipe.dto.response.RecipeResponse;
import com.swef.cookcode.user.domain.QUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class RecipeRepositoryImpl implements RecipeCustomRepository{

    private final JPAQueryFactory queryFactory;

    private final QRecipe recipe = QRecipe.recipe;
    private final QRecipeIngred recipeIngred = QRecipeIngred.recipeIngred;
    private final QFridgeIngredient fridgeIngred = QFridgeIngredient.fridgeIngredient;
    private final QUser user = QUser.user;

    @Override
    public Page<RecipeResponse> findRecipes(Long fridgeId, Boolean isCookable, Pageable pageable) {

        JPAQuery<RecipeResponse> query = queryFactory.select(Projections.constructor(RecipeResponse.class, recipe, user, isCookableExpression().as("isCookable")))
                .from(recipe)
                .join(user).on(recipe.author.id.eq(user.id))
                .leftJoin(recipeIngred).on(recipe.id.eq(recipeIngred.recipe.id).and(recipeIngred.isNecessary.eq(true)))
                .leftJoin(fridgeIngred).on(fridgeIngred.fridge.id.eq(fridgeId).and(fridgeIngred.ingred.id.eq(recipeIngred.ingredient.id)))
                .groupBy(recipe.id);

        if (nonNull(isCookable) && isCookable) {
            query.having(isCookableExpression().eq(true));
        }

        List<RecipeResponse> result = query.orderBy(recipe.createdAt.desc()).offset(pageable.getOffset()).limit(
                pageable.getPageSize()).fetch();

        return new PageImpl<>(result, pageable, result.size());
    }

    private BooleanExpression isCookableExpression() {
        return new CaseBuilder()
                .when(recipeIngred.ingredient.id.countDistinct().eq(fridgeIngred.id.countDistinct())).then(true)
                .otherwise(false);
    }
}
