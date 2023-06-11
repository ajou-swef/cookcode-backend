package com.swef.cookcode.user.repository;

import com.swef.cookcode.user.domain.Status;
import com.swef.cookcode.user.domain.User;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long>, UserCustomRepository {
    Optional<User> findByIdAndIsQuit(@Param("userId") Long userId, @Param("isQuit") Boolean isQuit);

    Optional<User> findByEmailAndIsQuit(@Param("email") String email, @Param("isQuit") Boolean isQuit);

    Optional<User> findByEmail(@Param("email") String email);

    @Query("update User u set u.status = :status where u.id = :userId")
    @Modifying
    void updateUserStatus(@Param("status") Status status, @Param("userId") Long userId);

    @Query("select case when count(s.id) >= 2 then true else false end from Subscribe s where s.publisher.id = :userId")
    boolean fulfillInfluencerCondition(@Param("userId") Long userId);

    @Query("select u from User u where u.status = 'INF_REQUESTED' or u.status = 'ADM_REQUESTED' order by u.updatedAt desc")
    List<User> getUsersByStatus();

    boolean existsByEmailAndIsQuit(@Param("email") String email, @Param("isQuit") Boolean isQuit);

    boolean existsByNicknameAndIsQuit(@Param("nickname") String nickname, @Param("isQuit") Boolean isQuit);

    boolean existsByIdAndIsQuit(@Param("userId") Long userId, @Param("isQuit") Boolean isQuit);
}
