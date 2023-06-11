package com.swef.cookcode.membership.service;

import com.swef.cookcode.membership.domain.Membership;
import com.swef.cookcode.membership.domain.MembershipJoin;
import com.swef.cookcode.membership.dto.MembershipCreateRequest;
import com.swef.cookcode.membership.dto.MembershipResponse;
import com.swef.cookcode.membership.repository.MembershipJoinRepository;
import com.swef.cookcode.membership.repository.MembershipRepository;
import com.swef.cookcode.user.domain.User;
import com.swef.cookcode.user.service.UserSimpleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final UserSimpleService userSimpleService;

    private final MembershipRepository membershipRepository;

    private final MembershipJoinRepository membershipJoinRepository;

    @Transactional
    public void createMembership(User user, MembershipCreateRequest request) {
        Membership membership = Membership.createEntity(user, request);

        membershipRepository.save(membership);
    }

    @Transactional(readOnly = true)
    public List<MembershipResponse> getMembership(Long createrId) {
        User creater = userSimpleService.getUserById(createrId);

        List<Membership> membershipList = membershipRepository.findByCreater(creater);
        return membershipList.stream().map(MembershipResponse::from).toList();
    }

    @Transactional
    public void joinMembership(User user, Long membershipId) {
        Membership membership = membershipRepository.getReferenceById(membershipId);

        MembershipJoin membershipJoin = MembershipJoin.createEntity(membership, user);

        membershipJoinRepository.save(membershipJoin);
    }
}
