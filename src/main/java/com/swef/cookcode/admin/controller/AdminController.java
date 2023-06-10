package com.swef.cookcode.admin.controller;

import com.swef.cookcode.admin.service.AdminService;
import com.swef.cookcode.common.ApiResponse;
import com.swef.cookcode.common.dto.EmailMessage;
import com.swef.cookcode.common.entity.CurrentUser;
import com.swef.cookcode.common.util.EmailUtil;
import com.swef.cookcode.user.domain.User;
import com.swef.cookcode.user.service.UserSimpleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/admin")
@RequiredArgsConstructor
@SuppressWarnings({"rawtypes", "unchecked"})
public class AdminController {

    private final AdminService adminService;

    private final UserSimpleService userSimpleService;

    private final EmailUtil emailUtil;

    @PatchMapping("/authorization/{userId}/{isAccept}")
    public ResponseEntity<ApiResponse> authorizePermissionOfUser(@CurrentUser User user, @PathVariable(value = "userId") Long userId, @PathVariable(value = "isAccept") Boolean isAccept) {
        User targetUser = userSimpleService.getUserById(userId);
        adminService.validateUserAuthorityForRequest(targetUser);
        EmailMessage message = adminService.createMessageForNotification(targetUser, isAccept);
        adminService.authorizeUser(user, targetUser, isAccept);
        emailUtil.sendMessage(message);
        ApiResponse apiResponse = ApiResponse.builder()
                .message("권한 처리 성공")
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
