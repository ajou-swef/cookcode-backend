package com.swef.cookcode.cookie.repository;

import com.swef.cookcode.cookie.dto.CookieResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CookieCustomRepository {
    Slice<CookieResponse> searchCookies(String query, Pageable pageable);
}
