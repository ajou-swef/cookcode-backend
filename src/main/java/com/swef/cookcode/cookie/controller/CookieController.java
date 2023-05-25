package com.swef.cookcode.cookie.controller;

import com.swef.cookcode.common.ApiResponse;
import com.swef.cookcode.common.SliceResponse;
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

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("api/v1/cookie")
@RequiredArgsConstructor
@SuppressWarnings({"rawtypes", "unchecked"})
public class CookieController {

    private final CookieService cookieService;

    private final int COOKIE_SLICE_SIZE = 5;

    @GetMapping
    public ResponseEntity<ApiResponse<SliceResponse<CookieResponse>>> getRandomCookies(
            @PageableDefault(size = COOKIE_SLICE_SIZE) Pageable pageable, @CurrentUser User user){

        Slice<CookieResponse> cookieSlice = cookieService.getRandomCookies(pageable);

        SliceResponse<CookieResponse> sliceResponse = new SliceResponse<>(cookieSlice);

        ApiResponse response = ApiResponse.builder()
                .message("쿠키 조회 성공")
                .status(HttpStatus.OK.value())
                .data(sliceResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{cookieId}")
    public ResponseEntity<ApiResponse<CookieResponse>> getCookieById(
            @PathVariable Long cookieId, @CurrentUser User user){

        CookieResponse data = cookieService.getCookieById(cookieId);

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


        SliceResponse<CookieResponse> response = new SliceResponse<>(cookieService.searchCookiesWith(query, pageable));
        ApiResponse apiResponse = ApiResponse.builder()
                .message("쿠키 검색 성공")
                .status(HttpStatus.OK.value())
                .data(response)
                .build();
        return ResponseEntity.ok().body(apiResponse);
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<SliceResponse<CookieResponse>>> getCookiesOfUser(
            @PathVariable Long userId,
            @PageableDefault(size = COOKIE_SLICE_SIZE, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){

        Slice<CookieResponse> cookieResponseSlice = cookieService.getCookiesOfUser(pageable, userId);

        SliceResponse<CookieResponse> sliceResponse = new SliceResponse<>(cookieResponseSlice);

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
}
