package com.swef.cookcode.cookie.controller;

import com.swef.cookcode.common.ApiResponse;
import com.swef.cookcode.common.SliceResponse;
import com.swef.cookcode.common.entity.CurrentUser;
import com.swef.cookcode.cookie.domain.Cookie;
import com.swef.cookcode.cookie.dto.CookieResponse;
import com.swef.cookcode.cookie.service.CookieService;
import com.swef.cookcode.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/cookie")
@RequiredArgsConstructor
public class CookieController {

    private final CookieService cookieService;

    private final int COOKIE_SLICE_SIZE = 5;

    @GetMapping
    public ResponseEntity<ApiResponse<SliceResponse<CookieResponse>>> getCookie(
            @CurrentUser User user,  @PageableDefault(size = COOKIE_SLICE_SIZE) Pageable pageable){

        Slice<Cookie> cookieSlice = cookieService.getCookies(pageable);
        Slice<CookieResponse> cookieRsponseSlice = cookieSlice.map(CookieResponse::of);

        SliceResponse<CookieResponse> sliceResponse = new SliceResponse<>(cookieRsponseSlice);

        ApiResponse response = ApiResponse.builder()
                .message("쿠키 조회 성공")
                .status(HttpStatus.OK.value())
                .data(sliceResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{cookieId}")
    public ResponseEntity<ApiResponse<CookieResponse>> getCookieById(
            @CurrentUser User user, @PathVariable Long cookieId){

        Cookie cookie = cookieService.getCookieById(cookieId);

        CookieResponse data = CookieResponse.of(cookie);

        ApiResponse response = ApiResponse.builder()
                .message("쿠키 단건 조회 성공")
                .status(HttpStatus.OK.value())
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }
}
