package com.swef.cookcode.cookie.repository;

import com.swef.cookcode.cookie.domain.Cookie;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CookieRepository extends JpaRepository<Cookie, Long>, CookieCustomRepository {

    @Modifying
    @Query("update Cookie c set c.recipe.id = null where c.recipe.id = :recipeId")
    void updateCookieWhenRecipeDeleted(@Param("recipeId") Long recipeId);

}
