package com.swef.cookcode.membership.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MembershipCreateRequest {

    private String grade;

    private Long price;

}
