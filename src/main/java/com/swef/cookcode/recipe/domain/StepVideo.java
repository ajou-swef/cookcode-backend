package com.swef.cookcode.recipe.domain;


import com.swef.cookcode.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "step_video")
@Getter
public class StepVideo extends BaseEntity {

    private static final int MAX_URL_LENGTH = 500;

    @Id
    @Column(name = "step_video_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id", nullable = false)
    private Step step;

    @Column(nullable = false, length = 300)
    private String videoUrl;

    @Builder
    public StepVideo(Step step, String videoUrl){
        this.step = step;
        this.videoUrl = videoUrl;
    }
}
