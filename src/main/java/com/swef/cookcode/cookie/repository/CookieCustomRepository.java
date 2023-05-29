package com.swef.cookcode.cookie.repository;

import com.swef.cookcode.cookie.dto.CookieResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import java.util.List;

public interface CookieCustomRepository {
    Slice<CookieResponse> searchCookies(String query, Long userId, Pageable pageable);

    List<CookieResponse> findRandomCookieResponse(Pageable pageable, Long userId);

    Slice<CookieResponse> findByTargetUserId(Pageable pageable, Long targetUserId, Long userId);

    CookieResponse findCookieResponseById(Long cookieId, Long userId);
}
