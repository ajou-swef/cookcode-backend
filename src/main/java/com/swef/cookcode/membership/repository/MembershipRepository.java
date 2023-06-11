package com.swef.cookcode.membership.repository;

import com.swef.cookcode.membership.domain.Membership;
import com.swef.cookcode.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

    List<Membership> findByCreater(User user);
}
