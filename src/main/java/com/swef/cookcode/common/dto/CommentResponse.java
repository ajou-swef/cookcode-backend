package com.swef.cookcode.common.dto;

import com.swef.cookcode.common.entity.Comment;
import com.swef.cookcode.user.dto.response.UserSimpleResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
public class CommentResponse {
    private final Long commentId;

    private final UserSimpleResponse user;

    private final String comment;

    public static CommentResponse from(Comment comment) {
        return CommentResponse.builder()
                .commentId(comment.getId())
                .user(UserSimpleResponse.from(comment.getUser()))
                .comment(comment.getComment())
                .build();
    }
}
