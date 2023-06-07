package com.swef.cookcode.cookie.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
@Setter
public class CookieCreateRequest {

    private String title;

    private String desc;

    private MultipartFile cookieVideo;

    private MultipartFile thumbnail;

    private Long recipeId;
}
