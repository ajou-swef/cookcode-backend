package com.swef.cookcode.user.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.swef.cookcode.common.ApiResponse;
import com.swef.cookcode.common.SliceResponse;
import com.swef.cookcode.common.dto.UrlResponse;
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
import com.swef.cookcode.user.service.UserService;
import com.swef.cookcode.user.service.UserSimpleService;
import jakarta.validation.Valid;
import java.net.URI;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

        UserDetailResponse data = userSimpleService.getInfoByUserId(user.getId(), userId);

        ApiResponse apiResponse = ApiResponse.builder()
                .message("유저 정보 조회 성공")
                .status(OK.value())
                .data(data)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/profileImage")
    public ResponseEntity<ApiResponse<UrlResponse>> updateProfileImage(@CurrentUser User user, @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        UrlResponse response = userService.updateProfileImage(user, profileImage);
        ApiResponse apiResponse = ApiResponse.builder()
                .message("유저의 프로필 이미지 수정 성공")
                .status(OK.value())
                .data(response)
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

        Slice<UserDetailResponse> users = userService.searchUsersWith(user.getId(), nickname, pageable);

        SliceResponse<UserDetailResponse> response = new SliceResponse<>(users);

        ApiResponse apiResponse = ApiResponse.builder()
                .message("유저 검색 성공")
                .status(OK.value())
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/subscribe/{createrId}")
    public ResponseEntity<ApiResponse> toggleSubscribe(@CurrentUser User user, @PathVariable Long createrId){

        userService.toggleSubscribe(user, createrId);

        ApiResponse apiResponse = ApiResponse.builder()
                .message("크리에이터 구독 상태 변경 성공")
                .status(OK.value())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/subscribe/subscribers")
    public ResponseEntity<ApiResponse<SliceResponse<UserDetailResponse>>> getSubscribers(
            @CurrentUser User user, Pageable pageable){

        Slice<UserDetailResponse> subscribers = userService.getSubscribers(pageable, user);
        SliceResponse<UserDetailResponse> response = new SliceResponse<>(subscribers);

        ApiResponse apiResponse = ApiResponse.builder()
                .message("사용자의 구독자 조회 성공")
                .status(OK.value())
                .data(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/subscribe/publishers")
    public ResponseEntity<ApiResponse<SliceResponse<UserDetailResponse>>> getPublishers(
            @CurrentUser User user, Pageable pageable){

        Slice<UserDetailResponse> publishers = userService.getPublishers(pageable, user);
        SliceResponse<UserDetailResponse> response = new SliceResponse<>(publishers);

        ApiResponse apiResponse = ApiResponse.builder()
                .message("구독한 크리에이터 조회 성공")
                .status(OK.value())
                .data(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

}
