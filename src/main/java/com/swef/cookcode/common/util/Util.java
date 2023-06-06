package com.swef.cookcode.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swef.cookcode.common.ErrorCode;
import com.swef.cookcode.common.dto.UrlResponse;
import com.swef.cookcode.common.error.exception.InvalidRequestException;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class Util {

    private final S3Util s3Util;

    private final ObjectMapper objectMapper;

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

    public void setResponse(int status, HttpServletResponse response, Object responseBody) {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try {
            String json = objectMapper.writeValueAsString(responseBody);
            PrintWriter writer = response.getWriter();
            writer.write(json);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
