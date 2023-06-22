package com.swef.cookcode;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import io.findify.s3mock.S3Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class S3MockConfig {

  @Value("${aws.s3.region}")
  private String region;

  @Bean
  public S3Mock s3Mock() {
    return new S3Mock.Builder().withPort(8001).withInMemoryBackend().build();
  }

  @Bean(destroyMethod = "shutdown")
  @Primary
  public AmazonS3Client amazonS3() {
    EndpointConfiguration endpoint = new EndpointConfiguration("http://localhost:8001", region);
    return (AmazonS3Client) AmazonS3ClientBuilder
        .standard()
        .withPathStyleAccessEnabled(true)
        .withEndpointConfiguration(endpoint)
        .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials())).build();
  }
}
