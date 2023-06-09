package com.swef.cookcode.common.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.swef.cookcode.common.ErrorCode;
import com.swef.cookcode.common.error.exception.InvalidRequestException;
import com.swef.cookcode.common.error.exception.S3Exception;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.swef.cookcode.common.ErrorCode.*;

@RequiredArgsConstructor
@Component
public class S3Util {

    private final int KEY_DELIMITER = 3;

    private final AmazonS3Client amazonS3Client;

    @Value("${aws.s3.bucketName}")
    private String bucket;

    public String upload(MultipartFile multipartFile, String dirName) {

        String fileName = dirName + "/" + new SimpleDateFormat("yyyyMMddHmsS").format(new Date()) + UUID.randomUUID();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getSize());

        try (InputStream inputStream = multipartFile.getInputStream()){
            return putInputStreamToS3(inputStream, fileName, objectMetadata);
        } catch (IOException e) {
            throw new S3Exception(MULTIPART_GETINPUTSTREAM_FAILED);
        }
    }

    private String putInputStreamToS3(InputStream inputStream, String fileName, ObjectMetadata objectMetadata){
        try {
            amazonS3Client.putObject(
                    new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
            );
            return amazonS3Client.getUrl(bucket, fileName).toString();
        } catch (AmazonS3Exception e) {
            throw new S3Exception(S3_UPLOAD_FAILED);
        }
    }

    public void deleteFile(String url){
        String[] tokens = url.split("/");
        if (tokens.length <= KEY_DELIMITER) throw new InvalidRequestException(ErrorCode.INVALID_URL);
        String key = String.join("/", Arrays.asList(tokens).subList(KEY_DELIMITER, tokens.length));

        amazonS3Client.deleteObject(bucket, key);
    }
}
