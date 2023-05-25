package com.swef.cookcode.cookie.repository;

import com.swef.cookcode.cookie.domain.Cookie;
import com.swef.cookcode.cookie.dto.CookieDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CookieRepository extends JpaRepository<Cookie, Long>, CookieCustomRepository {
    @Query(value = "SELECT c.cookie_id as cookieId, recipe_id as recipeId, c.title, c.description, c.video_url as videoUrl, c.created_at as createdAt, c.updated_at as updatedAt, u.user_id as userId, u.profile_image as profileImage, u.nickname  FROM cookie as c join users u on c.user_id = u.user_id ORDER BY RAND()", nativeQuery = true)
    Slice<CookieDto> findRandomCookies(Pageable pageable);

    @Query(value = "SELECT c FROM Cookie c join fetch c.user WHERE c.user.id = :userId")
    Slice<Cookie> findByUserId(Pageable pageable, Long userId);
}
