package com.swef.cookcode.recipe.service;

import static com.swef.cookcode.common.ErrorCode.STEP_FILES_NECESSARY;

import com.swef.cookcode.common.error.exception.InvalidRequestException;
import com.swef.cookcode.recipe.domain.Recipe;
import com.swef.cookcode.recipe.domain.Step;
import com.swef.cookcode.recipe.domain.StepPhoto;
import com.swef.cookcode.recipe.domain.StepVideo;
import com.swef.cookcode.recipe.dto.request.StepCreateRequest;
import com.swef.cookcode.recipe.dto.response.StepResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StepService {

    @Transactional
    public List<StepResponse> saveStepsForRecipe(Recipe recipe, List<StepCreateRequest> stepRequests) {
        List<StepResponse> responses = new ArrayList<>();
        stepRequests.forEach(request -> {
            if (request.getPhotos().size() + request.getVideos().size() == 0) throw new InvalidRequestException(STEP_FILES_NECESSARY);
            Step step = Step.builder()
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .seq(request.getSeq())
                    .recipe(recipe)
                    .build();
            recipe.addStep(step);
            List<StepPhoto> savedPhotos = savePhotoUrlsForStep(step, request.getPhotos());
            List<StepVideo> savedVideos = saveVideoUrlsForStep(step, request.getVideos());
            responses.add(StepResponse.from(step, savedPhotos, savedVideos));
        });
        return responses;
    }

    @Transactional
    List<StepPhoto> savePhotoUrlsForStep(Step step, List<String> photos) {
        photos.forEach(url -> {
            StepPhoto photo = StepPhoto.builder()
                    .step(step)
                    .photoUrl(url)
                    .build();
            step.addPhoto(photo);
        });
        return step.getPhotos();
    }

    @Transactional
    List<StepVideo> saveVideoUrlsForStep(Step step, List<String> videos) {
        videos.forEach(url -> {
            StepVideo video = StepVideo.builder()
                    .step(step)
                    .videoUrl(url)
                    .build();
            step.addVideo(video);
        });
        return step.getVideos();
    }
}
