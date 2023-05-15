package com.swef.cookcode.common;

import com.swef.cookcode.common.error.exception.InvalidRequestException;
import com.swef.cookcode.common.util.S3Util;
import com.swef.cookcode.recipe.domain.Recipe;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class Util {

    private final S3Util s3Util;

    public static <T> void validateDuplication(List<T> list1, List<T> list2) {
        Set<T> mergedSets = new HashSet<>() {{
            addAll(list1);
            addAll(list2);
        }};
        if (mergedSets.size() < list1.size() + list2.size()) {
            throw new InvalidRequestException(ErrorCode.DUPLICATED);
        }
    }

    public UrlResponse uploadFilesToS3(String directory, List<MultipartFile> files) {
        List<String> urls = new ArrayList<>();
        files.forEach(file -> {
            try {
                String path = s3Util.upload(file, Recipe.RECIPE_DIRECTORY_NAME);
                urls.add(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return UrlResponse.builder()
                .urls(urls)
                .build();
    }
}
