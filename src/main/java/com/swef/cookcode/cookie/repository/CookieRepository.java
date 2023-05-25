package com.swef.cookcode.cookie.repository;

import com.swef.cookcode.cookie.domain.Cookie;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CookieRepository extends JpaRepository<Cookie, Long>, CookieCustomRepository {
    @Query(value = "SELECT c.* FROM cookie as c join users u on c.user_id = u.user_id ORDER BY RAND()", nativeQuery = true)
    Slice<Cookie> findRandomCookies(Pageable pageable);

    @Query(value = "SELECT c FROM Cookie c WHERE c.user.id = :userId")
    Slice<Cookie> findByUserId(Pageable pageable, Long userId);
}
