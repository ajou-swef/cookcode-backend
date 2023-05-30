package com.swef.cookcode.user.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.swef.cookcode.common.ApiResponse;
import com.swef.cookcode.common.PageResponse;
import com.swef.cookcode.common.SliceResponse;
import com.swef.cookcode.common.entity.CurrentUser;
import com.swef.cookcode.common.jwt.JwtAuthenticationToken;
import com.swef.cookcode.common.jwt.JwtPrincipal;
import com.swef.cookcode.fridge.service.FridgeService;
import com.swef.cookcode.user.domain.User;
import com.swef.cookcode.user.dto.request.UserSignInRequest;
import com.swef.cookcode.user.dto.request.UserSignUpRequest;
import com.swef.cookcode.user.dto.response.SignInResponse;
import com.swef.cookcode.user.dto.response.SignUpResponse;
import com.swef.cookcode.user.dto.response.UniqueCheckResponse;
import com.swef.cookcode.user.dto.response.UserDetailResponse;
import com.swef.cookcode.user.dto.response.UserSimpleResponse;
import com.swef.cookcode.user.service.UserService;
import com.swef.cookcode.user.service.UserSimpleService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/account")
@RequiredArgsConstructor
@SuppressWarnings({"rawtypes", "unchecked"})
public class AccountController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private final FridgeService fridgeService;

    private final UserSimpleService userSimpleService;

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

        fridgeService.createFridgeOfUser(newUser);

        ApiResponse response = ApiResponse.builder()
                .message("회원가입 성공하였습니다.")
                .status(CREATED.value())
                .data(SignUpResponse.from(newUser))
                .build();
        return ResponseEntity.created(URI.create("/signup")).body(response);
    }

    @GetMapping("/check")
    public ResponseEntity<ApiResponse<UniqueCheckResponse>> checkNicknameValid(
            @RequestParam(value = "nickname") String nickname) {
        UniqueCheckResponse response = new UniqueCheckResponse(userSimpleService.checkNicknameUnique(nickname));
        ApiResponse apiResponse = ApiResponse.builder()
                .message("중복 검사가 완료되었습니다.")
                .status(OK.value())
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserDetailResponse>> getUserInfo(@CurrentUser User user, @PathVariable("userId") Long userId) {
        User returnedUser = userSimpleService.getUserById(userId);
        UserDetailResponse res = UserDetailResponse.from(returnedUser);
        ApiResponse apiResponse = ApiResponse.builder()
                .message("유저 정보 조회 성공")
                .status(OK.value())
                .data(res)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<UserDetailResponse>> quit(@CurrentUser User user) {
        User returnedUser = userService.quit(user);
        fridgeService.deleteFridgeOfUser(returnedUser);
        ApiResponse apiResponse = ApiResponse.builder()
                .message("계정 삭제 성공")
                .status(OK.value())
                .data(UserDetailResponse.from(returnedUser))
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<SliceResponse<UserDetailResponse>>> searchUsers(@CurrentUser User user,
                                                                                      @RequestParam(value = "nickname") String nickname,
                                                                                      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
                                                                                          Pageable pageable) {
        Slice<User> users = userService.searchUsersWith(nickname, pageable);
        SliceResponse<UserDetailResponse> response = new SliceResponse<>(users.map(UserDetailResponse::from));
        ApiResponse apiResponse = ApiResponse.builder()
                .message("유저 검색 성공")
                .status(OK.value())
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/subscribe/{createrId}")
    public ResponseEntity<ApiResponse> createSubscribe(@CurrentUser User user, @PathVariable Long createrId){

        userService.createSubscribe(user, createrId);

        ApiResponse apiResponse = ApiResponse.builder()
                .message("크리에이터 구독 성공")
                .status(OK.value())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

}
