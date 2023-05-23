package com.swef.cookcode.cookie.service;

import com.swef.cookcode.common.ErrorCode;
import com.swef.cookcode.common.error.exception.NotFoundException;
import com.swef.cookcode.cookie.domain.Cookie;
import com.swef.cookcode.cookie.dto.CookieResponse;
import com.swef.cookcode.cookie.repository.CookieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CookieService {

    private final CookieRepository cookieRepository;

    @Transactional
    public Slice<Cookie> getRandomCookies(Pageable pageable) {
        return cookieRepository.findRandomCookies(pageable);
    }

    @Transactional
    public Cookie getCookieById(Long cookieId) {
        return cookieRepository.findById(cookieId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.COOKIE_NOT_FOUND));
    }

    @Transactional
    public Slice<Cookie> getCookiesOfUser(Pageable pageable, Long userId) {
        return cookieRepository.findByUserId(pageable, userId);
    }
}
