package com.swef.cookcode.cookie.repository;

import com.swef.cookcode.cookie.domain.CookieLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CookieLikeRepository extends JpaRepository<CookieLike, Long> {

    Optional<CookieLike> findByUserIdAndCookieId(Long id, Long cookieId);

    void deleteByCookieId(Long cookieId);
}
