package com.datasynops.job.service;

import java.io.File;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.datasynops.job.dto.JobDto;
import com.datasynops.job.entity.Job;
import com.datasynops.job.entity.JobEnum;
import com.datasynops.job.repo.JobRepo;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class JobService {

    @Autowired
    JobRepo jobRepo;

    @Autowired
    S3Service s3Service;

    public Job createJob(JobDto jobDto) {
        Job job = new Job();
        job.setStatus(JobEnum.NEW);
        job.setStartedAt(Timestamp.from(Instant.now()));
        job.setDescription(jobDto.getDescription());
        job.setJobName(jobDto.getJobName());
        job.setCreatedBy(jobDto.getCreatedBy());
        return jobRepo.save(job);
    }

    public ResponseEntity<String> updateJob(Job job) {
        if (job.getStatus() != null && job.getStatus().equals(JobEnum.NEW)) {
            //get the input files and process

            jobRepo.save(job);
        }
        return ResponseEntity.ok().build();
    }

    public List<Job> fetchJobs() {
        return jobRepo.findAll();
    }
}
