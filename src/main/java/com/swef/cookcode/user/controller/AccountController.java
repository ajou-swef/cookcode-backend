package com.swef.cookcode.user.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.swef.cookcode.common.ApiResponse;
import com.swef.cookcode.common.jwt.JwtAuthenticationToken;
import com.swef.cookcode.common.jwt.JwtPrincipal;
import com.swef.cookcode.user.domain.User;
import com.swef.cookcode.user.dto.request.UserSignInRequest;
import com.swef.cookcode.user.dto.request.UserSignUpRequest;
import com.swef.cookcode.user.dto.response.SignInResponse;
import com.swef.cookcode.user.dto.response.SignUpResponse;
import com.swef.cookcode.user.service.UserService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/account")
@RequiredArgsConstructor
@SuppressWarnings({"rawtypes", "unchecked"})
public class AccountController {

    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<SignInResponse>> signIn(
            @RequestBody @Valid UserSignInRequest request) {

        JwtAuthenticationToken authToken = new JwtAuthenticationToken(request.getEmail(),
                request.getPassword());
        Authentication authentication = authenticationManager.authenticate(authToken);
        String refreshToken = (String) authentication.getDetails();
        JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();
        ApiResponse response = ApiResponse.builder()
                .message("로그인 성공하였습니다.")
                .status(OK.value())
                .data(SignInResponse.builder()
                        .userId(principal.getUser().getId())
                        .accessToken(principal.getAccessToken())
                        .refreshToken(refreshToken)
                        .build())
                .build();
        return ResponseEntity.ok()
                .body(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignUpResponse>> signUp(@RequestBody @Valid
                                                              UserSignUpRequest request) {
        User newUser = userService.signUp(request);
        ApiResponse response = ApiResponse.builder()
                .message("회원가입 성공하였습니다.")
                .status(CREATED.value())
                .data(SignUpResponse.from(newUser))
                .build();
        return ResponseEntity.created(URI.create("/signup")).body(response);
    }
}
