package com.swef.cookcode.recipe.dto.response;

import com.swef.cookcode.recipe.domain.Step;
import com.swef.cookcode.recipe.domain.StepPhoto;
import com.swef.cookcode.recipe.domain.StepVideo;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StepResponse {
    private Long stepId;

    private Long seq;

    private String title;

    private String description;

    private List<PhotoResponse> photos;

    private List<VideoResponse> videos;

    public static StepResponse from(Step step, List<StepPhoto> photos, List<StepVideo> videos) {
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
    public static List<PhotoResponse> photoFrom(List<StepPhoto> photos) {
        return photos.stream().map(photo ->
                        PhotoResponse.builder()
                                .stepPhotoId(photo.getId())
                                .photoUrl(photo.getPhotoUrl())
                                .build())
                .toList();
    }

    public static List<VideoResponse> videoFrom(List<StepVideo> videos) {
        return videos.stream().map(video ->
                        VideoResponse.builder()
                                .stepVideoId(video.getId())
                                .videoUrl(video.getVideoUrl()).build())
                .toList();
    }
}
