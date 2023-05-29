package com.swef.cookcode.recipe.repository;


import static com.swef.cookcode.common.Util.hasNextInSlice;
import static com.swef.cookcode.fridge.domain.QFridgeIngredient.fridgeIngredient;
import static com.swef.cookcode.recipe.domain.QRecipe.recipe;
import static com.swef.cookcode.recipe.domain.QRecipeIngred.recipeIngred;
import static com.swef.cookcode.recipe.domain.QRecipeLike.recipeLike;
import static java.util.Objects.nonNull;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swef.cookcode.recipe.domain.QRecipeLike;
import com.swef.cookcode.recipe.dto.response.RecipeResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@RequiredArgsConstructor
public class RecipeRepositoryImpl implements RecipeCustomRepository{

    private final JPAQueryFactory queryFactory;

    private final QRecipeLike recipeLikeForIsLike = new QRecipeLike("recipeLikeForIsLike");

    @Override
    public Slice<RecipeResponse> findRecipes(Long fridgeId, Long userId, Boolean isCookable, Pageable pageable) {

        JPAQuery<RecipeResponse> query = selectRecipesWithCookableAndLike(fridgeId, userId)
                .groupBy(recipe.id);
        filterIfCookable(isCookable, query);
        List<RecipeResponse> result = query.orderBy(recipe.createdAt.desc()).offset(pageable.getOffset()).limit(
                pageable.getPageSize()+1).fetch();

        return new SliceImpl<>(result, pageable, hasNextInSlice(result, pageable));
    }

    @Override
    public Slice<RecipeResponse> searchRecipes(Long fridgeId, Long userId, String searchQuery, Boolean isCookable, Pageable pageable) {
        JPAQuery<RecipeResponse> query = selectRecipesWithCookableAndLike(fridgeId, userId)
                .where(recipeSearchContains(searchQuery))
                .groupBy(recipe.id);
        filterIfCookable(isCookable, query);
        List<RecipeResponse> result = query.orderBy(recipe.createdAt.desc()).offset(pageable.getOffset()).limit(
                pageable.getPageSize()+1).fetch();

        return new SliceImpl<>(result, pageable, hasNextInSlice(result, pageable));
    }

    private JPAQuery<RecipeResponse> selectRecipesWithCookableAndLike(Long fridgeId, Long userId) {
        return queryFactory.select(Projections.constructor(RecipeResponse.class,
                        recipe,
                        isCookableExpression().as("isCookable"),
                        recipeLike.countDistinct().as("likeCount"),
                        isLikedExpression(userId).as("isLiked")
                ))
                .from(recipe)
                .join(recipe.author)
                .fetchJoin()
                .leftJoin(recipeIngred).on(recipe.id.eq(recipeIngred.recipe.id))
                .leftJoin(fridgeIngredient)
                .on(fridgeIngredient.fridge.id.eq(fridgeId)
                        .and(fridgeIngredient.ingred.id.eq(recipeIngred.ingredient.id))
                        .and(recipeIngred.isNecessary.isTrue()))
                .leftJoin(recipeLike).on(recipeLike.recipe.id.eq(recipe.id))
                .leftJoin(recipeLikeForIsLike).on(recipeLikeForIsLike.recipe.id.eq(recipe.id).and(recipeLikeForIsLike.user.id.eq(userId)));

    }

    private void filterIfCookable(Boolean isCookable, JPAQuery<RecipeResponse> query) {
        if (nonNull(isCookable) && isCookable) {
            query.having(isCookableExpression().eq(true));
        }
    }

    private BooleanExpression recipeSearchContains(String searchQuery) {
        return  recipe.title.containsIgnoreCase(searchQuery)
                .or(recipe.description.containsIgnoreCase(searchQuery))
                .or(recipeIngred.ingredient.name.containsIgnoreCase(searchQuery))
                .or(recipe.author.nickname.containsIgnoreCase(searchQuery));
    }

    private BooleanExpression isLikedExpression(Long userId) {
        return new CaseBuilder()
                .when(recipeLikeForIsLike.user.id.eq(userId))
                .then(true)
                .otherwise(false);
    }

    private BooleanExpression isCookableExpression() {
        return new CaseBuilder()
                .when(new CaseBuilder()
                        .when(recipeIngred.isNecessary.isTrue())
                        .then(recipeIngred.id)
                        .otherwise((Long) null)
                        .countDistinct().eq(fridgeIngredient.ingred.countDistinct())).then(true)
                .otherwise(false);
    }
}
