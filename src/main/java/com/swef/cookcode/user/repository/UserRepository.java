package com.swef.cookcode.user.repository;

import com.swef.cookcode.user.domain.User;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByIdAndIsQuit(@Param("userId") Long userId, @Param("isQuit") Boolean isQuit);

    Optional<User> findByEmailAndIsQuit(@Param("email") String email, @Param("isQuit") Boolean isQuit);

    boolean existsByEmailAndIsQuit(@Param("email") String email, @Param("isQuit") Boolean isQuit);

    boolean existsByNicknameAndIsQuit(@Param("nickname") String nickname, @Param("isQuit") Boolean isQuit);

    boolean existsByIdAndIsQuit(@Param("userId") Long userId, @Param("isQuit") Boolean isQuit);
}
