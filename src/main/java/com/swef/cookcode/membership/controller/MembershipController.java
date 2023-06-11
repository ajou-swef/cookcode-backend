package com.swef.cookcode.membership.controller;

import com.swef.cookcode.common.ApiResponse;
import com.swef.cookcode.common.entity.CurrentUser;
import com.swef.cookcode.membership.domain.Membership;
import com.swef.cookcode.membership.dto.JoiningMembershipResponse;
import com.swef.cookcode.membership.dto.MembershipCreateRequest;
import com.swef.cookcode.membership.dto.MembershipResponse;
import com.swef.cookcode.membership.service.MembershipService;
import com.swef.cookcode.user.domain.User;
import com.swef.cookcode.user.dto.response.UserSimpleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/membership")
public class MembershipController {

    private final MembershipService membershipService;

    @GetMapping("/{createrId}")
    public ResponseEntity<ApiResponse<List<MembershipResponse>>> getCreaterMembership(
            @CurrentUser User user, @PathVariable Long createrId){

        List<MembershipResponse> membershipList = membershipService.getCreaterMembership(createrId);

        ApiResponse apiResponse = ApiResponse.builder()
                .message("멤버십 조회 성공")
                .status(HttpStatus.OK.value())
                .data(membershipList)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createMembership(
            @CurrentUser User user, @RequestBody MembershipCreateRequest membershipCreateRequest){

        membershipService.createMembership(user, membershipCreateRequest);

        ApiResponse apiResponse = ApiResponse.builder()
                .message("멤버십 생성 성공")
                .status(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @PostMapping("/{membershipId}")
    public ResponseEntity<ApiResponse> joinMembership(
            @CurrentUser User user, @PathVariable Long membershipId){

        membershipService.joinMembership(user, membershipId);

        ApiResponse apiResponse = ApiResponse.builder()
                .message("멤버십 가입 성공")
                .status(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<JoiningMembershipResponse>>> getJoiningMemberships(
            @CurrentUser User user){

        List<JoiningMembershipResponse> membershipList = membershipService.getJoiningMemberships(user);

        ApiResponse apiResponse = ApiResponse.builder()
                .message("가입 멤버십 조회 성공")
                .status(HttpStatus.OK.value())
                .data(membershipList)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

}
