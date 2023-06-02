package com.swef.cookcode.cookie.domain;

import com.swef.cookcode.common.entity.BaseEntity;
import com.swef.cookcode.cookie.dto.CookieCreateRequest;
import com.swef.cookcode.recipe.domain.Recipe;
import com.swef.cookcode.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
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
    private Long id;

    @Column(name = "title", nullable = false, length = MAX_TITLE_LENGTH)
    private String title;

    @Column(name = "description", nullable = false, length = MAX_DESCRIPTION_LENGTH)
    private String description;

    @Column(name = "thumbnail_url", nullable = false, length = MAX_VIDEO_URL_LENGTH)
    private String thumbnailUrl;

    @Column(name = "video_url", nullable = false, length = MAX_VIDEO_URL_LENGTH)
    private String videoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    private Cookie(String title, String description, String thumbnailUrl, String videoUrl, User user, Recipe recipe) {
        this.title = title;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.videoUrl = videoUrl;
        this.user = user;
        this.recipe = recipe;
    }

    public static Cookie createEntity(CookieCreateRequest request, User user, String thumbnailUrl, String cookieUrl, Recipe recipe) {
        return new Cookie(request.getTitle(), request.getDesc(), thumbnailUrl, cookieUrl, user, recipe);
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateDesc(String desc) {
        this.description = desc;
    }
}
