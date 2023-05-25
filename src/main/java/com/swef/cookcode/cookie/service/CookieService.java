package com.swef.cookcode.cookie.service;

import com.swef.cookcode.common.error.exception.NotFoundException;
import com.swef.cookcode.common.util.S3Util;
import com.swef.cookcode.cookie.domain.Cookie;
import com.swef.cookcode.cookie.dto.CookieCreateRequest;
import com.swef.cookcode.cookie.dto.CookiePatchRequest;
import com.swef.cookcode.cookie.dto.CookieResponse;
import com.swef.cookcode.cookie.repository.CookieRepository;
import com.swef.cookcode.recipe.domain.Recipe;
import com.swef.cookcode.recipe.service.RecipeService;
import com.swef.cookcode.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import com.swef.cookcode.common.ErrorCode;

import static com.swef.cookcode.common.ErrorCode.COOKIE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CookieService {

    private final CookieRepository cookieRepository;

    private final S3Util s3Util;

    private final RecipeService recipeService;


    @Transactional(readOnly = true)
    public Slice<Cookie> getRandomCookies(Pageable pageable) {
        return cookieRepository.findRandomCookies(pageable);
    }

    @Transactional(readOnly = true)
    public Cookie getCookieById(Long cookieId) {
        return cookieRepository.findById(cookieId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.COOKIE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Slice<Cookie> getCookiesOfUser(Pageable pageable, Long userId) {
        return cookieRepository.findByUserId(pageable, userId);
    }

    @Transactional
    public void createCookie(User user, CookieCreateRequest request){
        String cookieUrl = s3Util.upload(request.getMultipartFile(), "cookie");

        Recipe recipe = recipeService.getRecipeOrNull(request.getRecipeId());

        Cookie cookie = Cookie.createEntity(request, user, cookieUrl, recipe);

        cookieRepository.save(cookie);
    }

    @Transactional
    public void updateCookie(Long cookieId, CookiePatchRequest request) {
        Cookie cookie = cookieRepository.findById(cookieId)
                .orElseThrow(()->new NotFoundException(COOKIE_NOT_FOUND));

        cookie.updateTitle(request.getTitle());
        cookie.updateDesc(request.getDesc());
    }

    @Transactional
    public void deleteCookie(Long cookieId) {
        cookieRepository.deleteById(cookieId);
    }

    @Transactional(readOnly = true)
    public Slice<CookieResponse> searchCookiesWith(String query, Pageable pageable) {
        return cookieRepository.searchCookies(query, pageable);
    }

}
