package com.swef.cookcode.cookie.repository;

import com.swef.cookcode.cookie.domain.Cookie;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CookieRepository extends JpaRepository<Cookie, Long> {
    @Query(value = "SELECT * FROM Cookie ORDER BY RAND()", nativeQuery = true)
    Slice<Cookie> findCookies(Pageable pageable);
}
