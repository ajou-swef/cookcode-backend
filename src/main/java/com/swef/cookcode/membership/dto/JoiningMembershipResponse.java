package com.swef.cookcode.membership.dto;

import com.swef.cookcode.membership.domain.Membership;
import com.swef.cookcode.user.domain.User;
import com.swef.cookcode.user.dto.response.UserSimpleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JoiningMembershipResponse {
    private final Long membershipId;

    private final String grade;

    private final Long price;

    private final UserSimpleResponse creater;

    public static JoiningMembershipResponse from(Membership membership) {
        return new JoiningMembershipResponse(membership.getId(), membership.getGrade(), membership.getPrice(), UserSimpleResponse.from(membership.getCreater()));
    }
}
