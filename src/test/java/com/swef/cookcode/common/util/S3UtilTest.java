package com.swef.cookcode.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.swef.cookcode.S3MockConfig;
import io.findify.s3mock.S3Mock;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@TestConfiguration
@TestInstance(Lifecycle.PER_CLASS)
@Import(S3MockConfig.class)
class S3UtilTest {

    @Autowired
    private AmazonS3Client amazonS3;

    @Autowired
    private S3Util s3Util = new S3Util(amazonS3);


    @Value("${aws.s3.bucketName}")
    private String BUCKET_NAME;
    private final String DIR_NAME = "test-dir";
    private final String FILE_NAME = "test-file";
    private final String FILE_CONTENT_TYPE = "text/plain";
    private String S3_URL;

    @BeforeAll
    void setUp(@Autowired S3Mock s3Mock, @Autowired AmazonS3 amazonS3) {
        S3_URL = "http://localhost:8001/"+BUCKET_NAME+"/"+DIR_NAME;
        s3Mock.start();
        amazonS3.createBucket(BUCKET_NAME);
    }

    @AfterAll
    void tearDown(@Autowired S3Mock s3Mock, @Autowired AmazonS3 amazonS3) {
        amazonS3.shutdown();
        s3Mock.stop();
    }

    PutObjectRequest buildPutObjectRequest() {
        String path = "test/02.txt";
        String contentType = "text/plain";
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(contentType);
        return new PutObjectRequest(BUCKET_NAME, path, new ByteArrayInputStream("".getBytes(
                StandardCharsets.UTF_8)), objectMetadata);
    }

    @Test
    @DisplayName("s3 import 테스트")
    void S3Import() throws IOException {
        // given
        PutObjectRequest putObjectRequest = buildPutObjectRequest();
        amazonS3.putObject(putObjectRequest);

        // when
        S3Object s3Object = amazonS3.getObject(BUCKET_NAME, putObjectRequest.getKey());

        // then
        assertThat(s3Object.getObjectMetadata().getContentType()).isEqualTo(putObjectRequest.getMetadata().getContentType());
        assertThat(new String(FileCopyUtils.copyToByteArray(s3Object.getObjectContent()))).isEqualTo("");
    }

    @Test
    @DisplayName("파일 업로드 테스트")
    void upload() {
        // given
        byte[] fileContent = "Hello, S3!".getBytes();
        MultipartFile multipartFile = new MockMultipartFile(FILE_NAME, FILE_NAME, FILE_CONTENT_TYPE, fileContent);

        // when
        String result = s3Util.upload(multipartFile, DIR_NAME);

        // then
        assertThat(result).contains(S3_URL);
    }

    @Test
    @DisplayName("파일 삭제 테스트")
    void deleteFile() {
    }
}