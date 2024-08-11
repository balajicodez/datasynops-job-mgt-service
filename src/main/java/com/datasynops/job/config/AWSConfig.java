package com.datasynops.job.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSConfig {

    // Injecting access key from application.properties
    @Value("${aws.s3.accesskey:AKIAQEFWA5FN2L3E2Z7V}")
    private String accessKey;

    // Injecting secret key from application.properties
    @Value("${aws.s3.secretAccesskey:KayxITuvnZBZ45vPejtAh+j/hHeISse2VpsAwa1f}")
    private String accessSecret;

    // Injecting region from application.properties
    @Value("${aws.region:ap-south-1}")
    private String region;

    // Creating a bean for Amazon S3 client
    @Bean
    public AmazonS3 s3Client() {
        // Creating AWS credentials using access key and secret key
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, accessSecret);
        
        // Building Amazon S3 client with specified credentials and region
        return AmazonS3ClientBuilder.standard()
        .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
    }
}
