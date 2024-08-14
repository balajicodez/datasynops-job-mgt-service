package com.datasynops.job.service;

import java.io.File;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import com.datasynops.job.dto.JobDto;
import com.datasynops.job.entity.Job;
import com.datasynops.job.entity.JobEnum;
import com.datasynops.job.repo.JobRepo;

@Service
public class JobService {

    @Autowired
    JobRepo jobRepo;

    @Autowired
    S3Service s3Service;

    @Value("${data.engine.baseurl}")
    private String dataEngineUrl;

    @Autowired
    RestTemplate restTemplate;

    public Job createJob(JobDto jobDto) {
        Job job = new Job();
        job.setStatus(JobEnum.NEW);
        job.setStartedAt(Timestamp.from(Instant.now()));
        job.setDescription(jobDto.getDescription());
        job.setJobName(jobDto.getJobName());
        job.setCreatedBy(jobDto.getCreatedBy());
        job.setPlatform(jobDto.getPlatform());
        job = jobRepo.save(job);

        // ResponseEntity<String> response = restTemplate
        // .getForEntity(dataEngineUrl + "/init/" + job.getId() + "-" +
        // job.getJobName(), String.class);

        Map<String, String> requestObject = new HashMap<String, String>();
        requestObject.put("project_id", job.getId() + "-" + job.getJobName());
        requestObject.put("platform", job.getPlatform());

        String response = restTemplate.postForObject(dataEngineUrl + "/init", requestObject,
                String.class);
        System.out.println(" resp "+response);
        job.setStatus(JobEnum.INIT);
        jobRepo.save(job);
        return job;
    }

    public ResponseEntity<String> updateJob(Job job) {        
        jobRepo.save(job);       
        return ResponseEntity.ok().build();
    }
    
    public List<Job> fetchJobs() {
        return jobRepo.findAll();
    }

    public Job fetchJob(Long jobId) {
        Optional<Job> optionalJob = jobRepo.findById(jobId);
        return optionalJob.get();
    }
}
