package com.swef.cookcode.user.repository;

import com.swef.cookcode.user.dto.response.UserDetailResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface UserCustomRepository {
    UserDetailResponse getInfoByUserId(Long userId, Long targetUserId);

    Slice<UserDetailResponse> findByNicknameContaining(Long userId, String searchQuery, Pageable pageable);

    Slice<UserDetailResponse> findSubscribers(Pageable pageable, Long userId);

    Slice<UserDetailResponse> findPublishers(Pageable pageable, Long userId);
}
