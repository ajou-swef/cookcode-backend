package com.swef.cookcode.common;

import com.swef.cookcode.common.error.exception.InvalidRequestException;
import com.swef.cookcode.common.util.S3Util;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class Util {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
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
        List<String> urls = files.stream().map(file -> s3Util.upload(file, directory)).toList();
        return UrlResponse.builder()
                .urls(urls)
                .build();
    }
    public void deleteFilesInS3(List<String> urls) {
        urls.forEach(s3Util::deleteFile);
    }

    public static <T> boolean hasNextInSlice(List<T> result, Pageable pageable) {
        boolean hasNext = false;
        if (result.size() > pageable.getPageSize()) {
            result.remove(pageable.getPageSize());
            hasNext = true;
        }
        return hasNext;
    }

    public static String createMixedCode(int size) {
        Random random = new Random();
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < size; i++) {
            int index = random.nextInt(4);

            switch (index) {
                case 0 -> key.append((char) ((int) random.nextInt(26) + 97));
                case 1 -> key.append((char) ((int) random.nextInt(26) + 65));
                default -> key.append(random.nextInt(9));
            }
        }
        return key.toString();
    }

    public static String createNumberCode(int size) {
        StringBuilder key = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            key.append(random.nextInt(10));
        }
        return key.toString();
    }
}
