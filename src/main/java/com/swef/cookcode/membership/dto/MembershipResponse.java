package com.swef.cookcode.membership.dto;

import com.swef.cookcode.membership.domain.Membership;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MembershipResponse {

    private final Long membershipId;

    private final String grade;

    private final Long price;

    public static MembershipResponse from(Membership membership) {
        return new MembershipResponse(membership.getId(), membership.getGrade(), membership.getPrice());
    }
}
