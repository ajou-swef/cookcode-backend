package com.swef.cookcode.recipe.service;

import com.swef.cookcode.recipe.domain.Recipe;
import com.swef.cookcode.recipe.domain.RecipeIngred;
import com.swef.cookcode.recipe.domain.Step;
import com.swef.cookcode.recipe.domain.StepPhoto;
import com.swef.cookcode.recipe.domain.StepVideo;
import com.swef.cookcode.recipe.dto.request.StepCreateRequest;
import com.swef.cookcode.recipe.dto.response.StepResponse;
import com.swef.cookcode.recipe.repository.StepPhotoRepository;
import com.swef.cookcode.recipe.repository.StepRepository;
import com.swef.cookcode.recipe.repository.StepVideoRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StepService {
    private final StepRepository stepRepository;

    private final StepPhotoRepository stepPhotoRepository;

    private final StepVideoRepository stepVideoRepository;

    @Transactional
    public List<StepResponse> saveStepsForRecipe(Recipe recipe, List<StepCreateRequest> stepRequests) {
        List<StepResponse> responses = new ArrayList<>();
        stepRequests.forEach(request -> {
            Step step = Step.builder()
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .seq(request.getSeq())
                    .recipe(recipe)
                    .build();
            Step savedStep = stepRepository.save(step);
            List<StepPhoto> savedPhotos = savePhotoUrlsForStep(savedStep, request.getPhotos());
            List<StepVideo> savedVideos = saveVideoUrlsForStep(savedStep, request.getVideos());
            responses.add(StepResponse.from(savedStep, savedPhotos, savedVideos));
        });
        return responses;
    }

    @Transactional
    List<StepPhoto> savePhotoUrlsForStep(Step step, List<String> photos) {
        List<StepPhoto> stepPhotos = photos.stream().map(url ->
                StepPhoto.builder()
                .step(step)
                .photoUrl(url)
                .build()
        ).toList();
        return stepPhotoRepository.saveAll(stepPhotos);
    }

    @Transactional
    List<StepVideo> saveVideoUrlsForStep(Step step, List<String> videos) {
        List<StepVideo> stepVideos = videos.stream().map(url ->
                StepVideo.builder()
                        .step(step)
                        .videoUrl(url)
                        .build()
        ).toList();
        return stepVideoRepository.saveAll(stepVideos);
    }
}
