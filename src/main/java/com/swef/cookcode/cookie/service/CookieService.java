package com.swef.cookcode.cookie.service;

import com.swef.cookcode.common.error.exception.NotFoundException;
import com.swef.cookcode.common.error.exception.PermissionDeniedException;
import com.swef.cookcode.common.util.S3Util;
import com.swef.cookcode.cookie.domain.Cookie;
import com.swef.cookcode.cookie.domain.CookieComment;
import com.swef.cookcode.cookie.domain.CookieLike;
import com.swef.cookcode.cookie.dto.CookieCommentResponse;
import com.swef.cookcode.cookie.dto.CookieCreateRequest;
import com.swef.cookcode.cookie.dto.CookiePatchRequest;
import com.swef.cookcode.cookie.dto.CookieResponse;
import com.swef.cookcode.cookie.repository.CookieCommentRepository;
import com.swef.cookcode.cookie.repository.CookieLikeRepository;
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

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.swef.cookcode.common.ErrorCode.*;
import static java.util.Objects.isNull;
import static com.swef.cookcode.common.ErrorCode.COOKIE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CookieService {

    private final CookieRepository cookieRepository;

    private final CookieLikeRepository cookieLikeRepository;

    private final CookieCommentRepository cookieCommentRepository;

    private final S3Util s3Util;

    private final RecipeService recipeService;


    @Transactional(readOnly = true)
    public List<CookieResponse> getRandomCookies(Pageable pageable, Long userId) {
        return cookieRepository.findRandomCookieResponse(pageable, userId);
    }

    @Transactional(readOnly = true)
    public CookieResponse getCookieById(Long cookieId, Long userId) {
        return cookieRepository.findCookieResponseById(cookieId, userId);
    }

    @Transactional(readOnly = true)
    public Slice<CookieResponse> getCookiesOfTargetUser(Pageable pageable, Long targetUserId, Long userId) {
        return cookieRepository.findByTargetUserId(pageable, targetUserId, userId);
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
        cookieLikeRepository.deleteByCookieId(cookieId);

        cookieCommentRepository.deleteByCookieId(cookieId);

        cookieRepository.deleteById(cookieId);
    }

    @Transactional
    public void toggleLike(User user, Long cookieId) {
        Optional<CookieLike> likeOptional = cookieLikeRepository.findByUserIdAndCookieId(user.getId(), cookieId);

        likeOptional.ifPresentOrElse(this::unlikeCookie, () -> likeCookie(user, cookieId));
    }

    void likeCookie(User user, Long cookieId) {
        Cookie cookie = cookieRepository.getReferenceById(cookieId);
        cookieLikeRepository.save(CookieLike.createEntity(user, cookie));
    }

    void unlikeCookie(CookieLike cookieLike) {
        cookieLikeRepository.delete(cookieLike);
    }

    @Transactional
    public void createCommentOfCookie(User user, Long cookieId, String comment) {

        Cookie cookie = cookieRepository.getReferenceById(cookieId);

        CookieComment cookieComment = CookieComment.createEntity(user, cookie, comment);

        cookieCommentRepository.save(cookieComment);
    }

    @Transactional(readOnly = true)
    public List<CookieCommentResponse> getCommentsOfCookie(Long cookieId) {
        List<CookieComment> cookieComments = cookieCommentRepository.findCookieComments(cookieId);

        return cookieComments.stream().map(CookieCommentResponse::of).toList();
    }

    @Transactional
    public void deleteCommentOfCookie(User user, Long commentId) {
        CookieComment cookieComment = cookieCommentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(COOKIE_COMMENT_NOT_FOUND));
        if(!Objects.equals(user.getId(), cookieComment.getUser().getId())){
            throw new PermissionDeniedException(COOKIE_COMMENT_USER_MISSMATCH);
        }
        cookieCommentRepository.deleteById(commentId);
    }

    @Transactional(readOnly = true)
    public Slice<CookieResponse> searchCookiesWith(String query, Long userId, Pageable pageable) {
        return cookieRepository.searchCookies(query, userId, pageable);
    }

}
