package com.swef.cookcode.recipe.repository;


import static com.swef.cookcode.common.util.Util.hasNextInSlice;
import static com.swef.cookcode.fridge.domain.QFridgeIngredient.fridgeIngredient;
import static com.swef.cookcode.recipe.domain.QRecipe.recipe;
import static com.swef.cookcode.recipe.domain.QRecipeComment.recipeComment;
import static com.swef.cookcode.recipe.domain.QRecipeIngred.recipeIngred;
import static com.swef.cookcode.recipe.domain.QRecipeLike.recipeLike;
import static com.swef.cookcode.fridge.domain.QFridge.fridge;
import static java.util.Objects.nonNull;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swef.cookcode.recipe.domain.QRecipeLike;
import com.swef.cookcode.recipe.dto.response.RecipeDetailResponse;
import com.swef.cookcode.recipe.dto.response.RecipeResponse;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@RequiredArgsConstructor
public class RecipeRepositoryImpl implements RecipeCustomRepository{

    private final JPAQueryFactory queryFactory;

    private final QRecipeLike recipeLikeForIsLike = new QRecipeLike("recipeLikeForIsLike");

    @Override
    public Slice<RecipeResponse> findRecipes(Long userId, Boolean isCookable, Pageable pageable) {

        JPAQuery<RecipeResponse> query = selectRecipesWithCookableAndLike(userId)
                .groupBy(recipe.id);
        filterIfCookable(isCookable, query);
        List<RecipeResponse> result = query.orderBy(recipe.createdAt.desc()).offset(pageable.getOffset()).limit(
                pageable.getPageSize()+1).fetch();

        return new SliceImpl<>(result, pageable, hasNextInSlice(result, pageable));
    }

    @Override
    public Slice<RecipeResponse> findRecipesOfUser(Long userId, Long targetUserId, Pageable pageable) {
        JPAQuery<RecipeResponse> query = selectRecipesWithCookableAndLike(userId)
                .where(recipe.author.id.eq(targetUserId))
                .groupBy(recipe.id);
        List<RecipeResponse> result = query.orderBy(recipe.createdAt.desc()).offset(pageable.getOffset()).limit(
                pageable.getPageSize()+1).fetch();
        return new SliceImpl<>(result, pageable, hasNextInSlice(result, pageable));
    }

    @Override
    public Optional<RecipeResponse> findRecipeById(Long userId, Long recipeId) {
        RecipeResponse response = selectDetailRecipeWithCookableAndLike(userId)
                .where(recipe.id.eq(recipeId))
                .groupBy(recipe.id)
                .fetchFirst();
        return Optional.ofNullable(response);
    }

    @Override
    public Slice<RecipeResponse> searchRecipes(Long userId, String searchQuery, Boolean isCookable, Pageable pageable) {
        JPAQuery<RecipeResponse> query = selectRecipesWithCookableAndLike(userId)
                .where(recipeSearchContains(searchQuery))
                .groupBy(recipe.id);
        filterIfCookable(isCookable, query);
        List<RecipeResponse> result = query.orderBy(recipe.createdAt.desc()).offset(pageable.getOffset()).limit(
                pageable.getPageSize()+1).fetch();

        return new SliceImpl<>(result, pageable, hasNextInSlice(result, pageable));
    }

    private JPAQuery<RecipeResponse> selectRecipesWithCookableAndLike(Long userId) {
        return selectRecipesWithCookableAndLike(userId, RecipeResponse.class);
    }

    private JPAQuery<RecipeDetailResponse> selectDetailRecipeWithCookableAndLike(Long userId) {
        return selectRecipesWithCookableAndLike(userId, RecipeDetailResponse.class);
    }

    private <T> JPAQuery<T> selectRecipesWithCookableAndLike(Long userId, Class<T> tClass) {
        return queryFactory.select(Projections.constructor(tClass,
                        recipe,
                        isCookableExpression().as("isCookable"),
                        recipeLike.countDistinct().as("likeCount"),
                        isLikedExpression(userId).as("isLiked"),
                        recipeComment.id.countDistinct().as("commentCount")
                ))
                .from(recipe)
                .join(recipe.author)
                .fetchJoin()
                .leftJoin(recipeIngred).on(recipe.id.eq(recipeIngred.recipe.id))
                .leftJoin(fridgeIngredient)
                .on(fridgeIngredient.fridge.id.eq(getFridgeIdOfUser(userId))
                        .and(fridgeIngredient.ingred.id.eq(recipeIngred.ingredient.id))
                        .and(recipeIngred.isNecessary.isTrue()))
                .leftJoin(recipeLike).on(recipeLike.recipe.id.eq(recipe.id))
                .leftJoin(recipeLikeForIsLike).on(recipeLikeForIsLike.recipe.id.eq(recipe.id).and(recipeLikeForIsLike.user.id.eq(userId)))
                .leftJoin(recipeComment).on(recipeComment.recipe.id.eq(recipe.id));

    }

    private void filterIfCookable(Boolean isCookable, JPAQuery<RecipeResponse> query) {
        if (nonNull(isCookable) && isCookable) {
            query.having(isCookableExpression().eq(true));
        }
    }

    private JPAQuery<Long> getFridgeIdOfUser(Long userId) {
        return queryFactory.select(fridge.id).from(fridge).where(fridge.owner.id.eq(userId));
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
