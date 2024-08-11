package com.datasynops.job.service;

import org.springframework.stereotype.Service;

import java.io.InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import com.amazonaws.services.s3.AmazonS3;
import reactor.core.publisher.Mono;


@Service
public class S3Service {

    private final String rootBucket = "affordablemr";

    @Autowired
    private AmazonS3 s3Client;

    public void upload(String path, String fileName, Mono<InputStream> inputStreamMono) throws Exception{
      
      s3Client.putObject(rootBucket, path+"/",  "");

      s3Client.putObject(rootBucket, path+"/"+fileName,  inputStreamMono.toFuture().get(), null );
    
    }
}
