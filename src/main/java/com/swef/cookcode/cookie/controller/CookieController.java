package com.swef.cookcode.cookie.controller;

import com.swef.cookcode.common.ApiResponse;
import com.swef.cookcode.common.SliceResponse;
import com.swef.cookcode.common.dto.CommentCreateRequest;
import com.swef.cookcode.common.dto.CommentResponse;
import com.swef.cookcode.common.entity.CurrentUser;
import com.swef.cookcode.cookie.dto.CookieCreateRequest;
import com.swef.cookcode.cookie.dto.CookiePatchRequest;
import com.swef.cookcode.cookie.dto.CookieResponse;
import com.swef.cookcode.cookie.service.CookieService;
import com.swef.cookcode.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("api/v1/cookie")
@RequiredArgsConstructor
public class CookieController {

    private final CookieService cookieService;

    private final int COOKIE_SLICE_SIZE = 5;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CookieResponse>>> getRandomCookies(
            @CurrentUser User user, @PageableDefault(size = COOKIE_SLICE_SIZE) Pageable pageable){

        List<CookieResponse> cookies = cookieService.getRandomCookies(pageable, user.getId());

        ApiResponse response = ApiResponse.builder()
                .message("쿠키 조회 성공")
                .status(HttpStatus.OK.value())
                .data(cookies)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{cookieId}")
    public ResponseEntity<ApiResponse<CookieResponse>> getCookieById(
            @CurrentUser User user, @PathVariable Long cookieId){

        CookieResponse data = cookieService.getCookieById(cookieId, user.getId());

        ApiResponse response = ApiResponse.builder()
                .message("쿠키 단건 조회 성공")
                .status(HttpStatus.OK.value())
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<SliceResponse<CookieResponse>>> searchCookies(@CurrentUser User user, @RequestParam(value = "query") String query,
                                                                                    @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        SliceResponse<CookieResponse> response = new SliceResponse<>(cookieService.searchCookiesWith(query, user.getId(), pageable));

        ApiResponse apiResponse = ApiResponse.builder()
                .message("쿠키 검색 성공")
                .status(HttpStatus.OK.value())
                .data(response)
                .build();
        return ResponseEntity.ok().body(apiResponse);
    }


    @GetMapping("/user/{targetUserId}")
    public ResponseEntity<ApiResponse<SliceResponse<CookieResponse>>> getCookiesOfUser(
            @CurrentUser User user,
            @PathVariable Long targetUserId,
            @PageableDefault(size = COOKIE_SLICE_SIZE, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){

        Slice<CookieResponse> cookieSlice = cookieService.getCookiesOfTargetUser(pageable, targetUserId, user.getId());

        SliceResponse<CookieResponse> sliceResponse = new SliceResponse<>(cookieSlice);

        ApiResponse response = ApiResponse.builder()
                .message("유저의 쿠키 조회 성공")
                .status(HttpStatus.OK.value())
                .data(sliceResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createCookie(
            @CurrentUser User user,
            @ModelAttribute CookieCreateRequest cookieCreateRequest){

        cookieService.createCookie(user, cookieCreateRequest);

        ApiResponse response = ApiResponse.builder()
                .message("쿠키 생성 성공")
                .status(CREATED.value())
                .build();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{cookieId}")
    public ResponseEntity<ApiResponse> updateCookie(
            @PathVariable Long cookieId,
            @RequestBody CookiePatchRequest cookiePatchRequest){

        cookieService.updateCookie(cookieId, cookiePatchRequest);

        ApiResponse response = ApiResponse.builder()
                .message("쿠키 업데이트 성공")
                .status(OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{cookieId}")
    public ResponseEntity<ApiResponse> deleteCookie(@PathVariable Long cookieId){

        cookieService.deleteCookie(cookieId);

        ApiResponse response = ApiResponse.builder()
                .message("쿠키 삭제 성공")
                .status(OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{cookieId}/likes")
    public ResponseEntity<ApiResponse> toggleLike(@CurrentUser User user, @PathVariable Long cookieId){

        cookieService.toggleLike(user, cookieId);

        ApiResponse response = ApiResponse.builder()
                .message("쿠키 좋아요 수정 성공")
                .status(OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{cookieId}/comments")
    public ResponseEntity<ApiResponse> createCommentOfCookie(@CurrentUser User user, @PathVariable Long cookieId, @RequestBody CommentCreateRequest comment){

        cookieService.createCommentOfCookie(user, cookieId, comment.getComment());

        ApiResponse response = ApiResponse.builder()
                .message("쿠키 댓글 생성 성공")
                .status(CREATED.value())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{cookieId}/comments")
    public ResponseEntity<ApiResponse<SliceResponse<CommentResponse>>> getCommentsOfCookie(
            @CurrentUser User user, @PathVariable Long cookieId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Slice<CommentResponse> cookieCommentSlice = cookieService.getCommentsOfCookie(pageable, cookieId);

        SliceResponse<CommentResponse> cookieCommentResponseSliceResponse = new SliceResponse<>(cookieCommentSlice);

        ApiResponse response = ApiResponse.builder()
                .message("쿠키 댓글 조회 성공")
                .status(OK.value())
                .data(cookieCommentResponseSliceResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse> deleteCommentsOfCookie(@CurrentUser User user, @PathVariable Long commentId){

        cookieService.deleteCommentOfCookie(user, commentId);

        ApiResponse response = ApiResponse.builder()
                .message("쿠키 댓글 삭제 성공")
                .status(OK.value())
                .build();

        return ResponseEntity.ok(response);
    }
}
