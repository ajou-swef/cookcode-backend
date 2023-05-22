package com.swef.cookcode.common.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.swef.cookcode.common.ErrorCode;
import com.swef.cookcode.common.error.exception.InvalidRequestException;
import com.swef.cookcode.common.error.exception.S3Exception;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@RequiredArgsConstructor
@Component
public class S3Util {

    private final int KEY_DELIMITER = 3;

    private final AmazonS3Client amazonS3Client;

    @Value("${aws.s3.bucketName}")
    private String bucket;

    public String upload(MultipartFile multipartFile, String dirName){
        String contentType = multipartFile.getContentType();
        File uploadFile = convert(multipartFile);

        try {
            FileInputStream uploadFileStream = new FileInputStream(uploadFile);

            String fileName = dirName + "/" + uploadFile.getName();
            String uploadImageUrl = putS3(uploadFileStream, fileName, contentType);

            removeNewFile(uploadFile);

            return uploadImageUrl;
        } catch (FileNotFoundException e) {
            throw new S3Exception(ErrorCode.STREAM_CONVERT_FAILED);
        }
    }

    private String putS3(FileInputStream uploadFileStream, String fileName, String contentType) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(contentType);
        amazonS3Client.putObject(
                new PutObjectRequest(bucket, fileName, uploadFileStream, objectMetadata)
        );
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(File targetFile) {
        targetFile.delete();
    }

    private File convert(MultipartFile file) {
        try {
            String originalFilename = new SimpleDateFormat("yyyyMMddHmsS").format(new Date()) + UUID.randomUUID();
            File convertFile = new File(originalFilename);

            if(!convertFile.createNewFile()){
                throw new S3Exception(ErrorCode.MULTIPART_CONVERT_FAILED);
            }

            FileOutputStream fos = new FileOutputStream(convertFile);
            fos.write(file.getBytes());

            return convertFile;
        } catch (IOException e) {
            throw new S3Exception(ErrorCode.MULTIPART_CONVERT_FAILED);
        }
    }

    public void deleteFile(String url){
        String[] tokens = url.split("/");
        if (tokens.length <= KEY_DELIMITER) throw new InvalidRequestException(ErrorCode.INVALID_URL);
        String key = String.join("/", Arrays.asList(tokens).subList(KEY_DELIMITER, tokens.length));

        amazonS3Client.deleteObject(bucket, key);
    }
}
