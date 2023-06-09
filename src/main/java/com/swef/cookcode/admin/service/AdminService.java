package com.swef.cookcode.admin.service;

import com.swef.cookcode.common.entity.CurrentUser;
import com.swef.cookcode.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    @Transactional
    public void authorizeUser(User user, Long userId, Boolean isAccept) {

    }
}
