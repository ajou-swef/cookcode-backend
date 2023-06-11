package com.swef.cookcode.membership.repository;

import com.swef.cookcode.membership.domain.MembershipJoin;
import com.swef.cookcode.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MembershipJoinRepository extends JpaRepository<MembershipJoin, Long> {

    @Query("select mj from MembershipJoin mj join fetch mj.membership m join fetch m.creater where mj.subscriber = :user")
    List<MembershipJoin> findBySubscriber(User user);

}
