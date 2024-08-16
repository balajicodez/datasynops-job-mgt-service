package com.datasynops.job.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

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

    public String get(String path, String fileName){
     return convertS3ObjectToString( s3Client.getObject(rootBucket , path+"/"+fileName));
    }

    public String convertS3ObjectToString(S3Object s3Object) {
        S3ObjectInputStream s3InputStream = s3Object.getObjectContent();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(s3InputStream))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                s3InputStream.close(); // Always close the input stream to avoid resource leaks
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
