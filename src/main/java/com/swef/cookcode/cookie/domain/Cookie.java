package com.swef.cookcode.cookie.domain;

import com.swef.cookcode.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cookie")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Cookie extends BaseEntity {

    private static final int MAX_TITLE_LENGTH = 30;
    private static final int MAX_DESCRIPTION_LENGTH = 30;
    private static final int MAX_VIDEO_URL_LENGTH = 300;

    @Id
    @Column(name = "cookie_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cookieId;

    @Column(name = "title", nullable = false, length = MAX_TITLE_LENGTH)
    private String title;

    @Column(name = "description", nullable = false, length = MAX_DESCRIPTION_LENGTH)
    private String description;

    @Column(name = "video_url", nullable = false, length = MAX_VIDEO_URL_LENGTH)
    private String videoUrl;

}
