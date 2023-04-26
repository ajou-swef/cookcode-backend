package com.swef.cookcode.recipe.dto.response;

import com.swef.cookcode.recipe.domain.Step;
import com.swef.cookcode.recipe.domain.StepPhoto;
import com.swef.cookcode.recipe.domain.StepVideo;
import java.util.Arrays;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StepResponse {
    private Long stepId;

    private Long seq;

    private String title;

    private String description;

    private PhotoResponse[] photos;

    private VideoResponse[] videos;

    public static StepResponse from(Step step, StepPhoto[] photos, StepVideo[] videos) {
        return StepResponse.builder()
                .stepId(step.getId())
                .seq(step.getSeq())
                .title(step.getTitle())
                .description(step.getDescription())
                .photos(photoFrom(photos))
                .videos(videoFrom(videos))
                .build();
    }

    // TODO : 중복코드 refactor
    public static PhotoResponse[] photoFrom(StepPhoto[] photos) {
        return (PhotoResponse[]) Arrays.stream(photos).map(photo ->
                        PhotoResponse.builder()
                                .stepPhotoId(photo.getId())
                                .photoUrl(photo.getPhotoUrl()))
                .toArray();
    }

    public static VideoResponse[] videoFrom(StepVideo[] videos) {
        return (VideoResponse[]) Arrays.stream(videos).map(video ->
                        VideoResponse.builder()
                                .stepVideoId(video.getId())
                                .videoUrl(video.getVideoUrl()))
                .toArray();
    }
}
