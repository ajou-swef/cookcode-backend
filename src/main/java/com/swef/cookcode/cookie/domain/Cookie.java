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

    @Id
    @Column(name = "cookie_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cookieId;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "video_url")
    private String videoUrl;

}
