package com.swef.cookcode.membership.service;

import com.swef.cookcode.membership.domain.Membership;
import com.swef.cookcode.membership.dto.MembershipCreateRequest;
import com.swef.cookcode.membership.repository.MembershipRepository;
import com.swef.cookcode.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipRepository membershipRepository;

    @Transactional
    public void createMembership(User user, MembershipCreateRequest request) {
        Membership membership = Membership.createEntity(user, request);

        membershipRepository.save(membership);
    }

    public List<Membership> getMembership(User user) {
        return membershipRepository.findByCreater(user);
    }
}
