package com.swef.cookcode.membership.repository;

import com.swef.cookcode.membership.domain.MembershipJoin;
import com.swef.cookcode.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MembershipJoinRepository extends JpaRepository<MembershipJoin, Long> {

    List<MembershipJoin> findBySubscriber(User user);
}
