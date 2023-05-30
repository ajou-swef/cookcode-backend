package com.swef.cookcode.common.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CommentCreateRequest {
    @NotBlank
    private String comment;
}
