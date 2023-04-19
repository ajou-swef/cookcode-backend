package com.swef.cookcode.cookie.repository;

import com.swef.cookcode.cookie.domain.Cookie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CookieRepository extends JpaRepository<Cookie, Long> {
}
