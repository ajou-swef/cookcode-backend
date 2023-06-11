package com.swef.cookcode.membership.repository;

import com.swef.cookcode.membership.domain.MembershipJoin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipJoinRepository extends JpaRepository<MembershipJoin, Long> {

}
