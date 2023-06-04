package com.swef.cookcode.user.repository;

import com.swef.cookcode.user.dto.response.UserDetailResponse;

public interface UserCustomRepository {
    UserDetailResponse getInfoByUserId(Long userId, Long targetUserId);
}
