package com.swef.cookcode.admin.service;

import static java.util.Objects.isNull;

import com.swef.cookcode.admin.dto.PermissionResponse;
import com.swef.cookcode.common.ErrorCode;
import com.swef.cookcode.common.dto.EmailMessage;
import com.swef.cookcode.common.error.exception.InvalidRequestException;
import com.swef.cookcode.common.error.exception.NotFoundException;
import com.swef.cookcode.common.error.exception.PermissionDeniedException;
import com.swef.cookcode.user.domain.Authority;
import com.swef.cookcode.user.domain.Status;
import com.swef.cookcode.user.domain.User;
import com.swef.cookcode.user.repository.UserRepository;
import com.swef.cookcode.user.service.UserService;
import com.swef.cookcode.user.service.UserSimpleService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    // TODO : 권한 처리 security로

    private final UserRepository userRepository;

    private final UserService userService;

    public void validateUserAuthorityForRequest(User targetUser) {
        Authority authority = targetUser.getStatus().getAuthority();
        if (isNull(authority)) throw new NotFoundException(ErrorCode.INVALID_AUTHORITY);
    }
    @Transactional
    public void authorizeUser(User user, User targetUser, Boolean isAccept) {
        if (user.getId().equals(targetUser.getId())) throw new InvalidRequestException(ErrorCode.UPGRADE_MYSELF);
        if (isAccept) {
            userService.validateInitialConditionOfInfluencer(targetUser.getId());
            targetUser.updateAuthority(targetUser.getStatus().getAuthority());
        }
        targetUser.changeStatus(Status.VALID);
        userRepository.save(targetUser);
    }

    public EmailMessage createMessageForNotification(User receiver, Boolean isAccept) {
        String authority = receiver.getStatus().getAuthority().getConsoleValue();
        String title = "[cookcode] "+authority+" 권한 심사 결과 안내해드립니다.";
        String content;
        if (isAccept) {
            content = receiver.getNickname() + "님, 축하드립니다.<br/>귀하는 " + authority + " 권한 심사에 통과되었습니다.<br/>귀하의 활발한 활동을 기대하며, 앞으로도 응원하겠습니다.";
        }
        else {
            content = receiver.getNickname() + "님, 죄송합니다.<br/>귀하는 " + authority + " 권한 심사에서 탈락되었습니다.<br/>";
        }
        return EmailMessage.createMessage(receiver.getEmail(), title, content);
    }

    @Transactional
    public List<PermissionResponse> getPermissions() {
        List<User> users = userRepository.getUsersByStatus();
        return users.stream().map(PermissionResponse::from).toList();
    }
}
