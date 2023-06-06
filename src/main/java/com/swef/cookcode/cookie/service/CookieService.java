package com.swef.cookcode.cookie.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.swef.cookcode.common.dto.CommentResponse;
import com.swef.cookcode.common.error.exception.NotFoundException;
import com.swef.cookcode.common.error.exception.PermissionDeniedException;
import com.swef.cookcode.common.error.exception.ThumbnailException;
import com.swef.cookcode.common.util.S3Util;
import com.swef.cookcode.common.util.ThumbnailUtil;
import com.swef.cookcode.cookie.domain.Cookie;
import com.swef.cookcode.cookie.domain.CookieComment;
import com.swef.cookcode.cookie.domain.CookieLike;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import java.util.Optional;

import static com.swef.cookcode.common.ErrorCode.*;
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
    public void createCookie(User user, CookieCreateRequest request) {

        String thumbnailUrl = extractAndUploadThumbnail(request.getMultipartFile());

        String cookieUrl = s3Util.upload(request.getMultipartFile(), "cookie");

        Recipe recipe = recipeService.getRecipeOrNull(request.getRecipeId());

        Cookie cookie = Cookie.createEntity(request, user, thumbnailUrl, cookieUrl, recipe);

        cookieRepository.save(cookie);
    }

    private String extractAndUploadThumbnail(MultipartFile multiPartFile){
        InputStream thumbnail;
        try {
            thumbnail = ThumbnailUtil.extractFirstFrame(multiPartFile.getInputStream());
        } catch (IOException e) {
            throw new ThumbnailException(MULTIPART_GETINPUTSTREAM_FAILED);
        }
        String thumbnailFileName = "cookie_thumbnail/" + new SimpleDateFormat("yyyyMMddHmsS").format(new Date()) + UUID.randomUUID();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType("image/png");

        return s3Util.putInputStreamToS3(thumbnail, thumbnailFileName, objectMetadata);
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
        cookieRepository.findById(cookieId).orElseThrow(() -> new NotFoundException(COOKIE_NOT_FOUND));

        Cookie cookie = cookieRepository.getReferenceById(cookieId);

        CookieComment cookieComment = CookieComment.createEntity(user, cookie, comment);

        cookieCommentRepository.save(cookieComment);
    }

    @Transactional(readOnly = true)
    public Slice<CommentResponse> getCommentsOfCookie(Pageable pageable, Long cookieId) {
        cookieRepository.findById(cookieId).orElseThrow(() -> new NotFoundException(COOKIE_NOT_FOUND));

        Slice<CookieComment> cookieComments = cookieCommentRepository.findCookieComments(pageable, cookieId);

        return cookieComments.map(CommentResponse::from);
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
