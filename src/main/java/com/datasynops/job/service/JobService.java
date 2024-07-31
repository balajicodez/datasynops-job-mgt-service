package com.datasynops.job.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.datasynops.job.dto.JobDto;
import com.datasynops.job.entity.Job;
import com.datasynops.job.entity.JobEnum;
import com.datasynops.job.repo.JobRepo;

@Service
public class JobService {

    @Autowired
    JobRepo jobRepo;

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
