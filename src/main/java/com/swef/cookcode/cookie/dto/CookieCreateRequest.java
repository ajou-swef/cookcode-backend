package com.swef.cookcode.cookie.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
public class CookieCreateRequest {

    private String title;

    private String desc;

    private MultipartFile multipartFile;

    private Long recipeId;
}
