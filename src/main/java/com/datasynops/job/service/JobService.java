package com.datasynops.job.service;

import com.datasynops.job.dto.JobDto;
import com.datasynops.job.entity.Job;
import com.datasynops.job.entity.JobEnum;
import com.datasynops.job.repo.JobRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    @Autowired
    JobRepo jobRepo;

    @Autowired
    ExcelProcessingService excelFileProcessingService;

    public Job createJob(JobDto jobDto) {
        Job job = new Job();
        job.setStatus(JobEnum.NEW);
        job.setStartedAt(Timestamp.from(Instant.now()));
        job.setOptionalText(jobDto.getOptionalTextString().getBytes());
        job.setJobName(jobDto.getJobName());
        job.setJobType(jobDto.getJobType());
        job.setCreatedBy(jobDto.getCreatedBy());
        return jobRepo.save(job);
    }

    public ResponseEntity<String> updateJob(Job job) {
        if (job.getStatus() != null && job.getStatus().equals(JobEnum.NEW)) {
            job.setStatus(JobEnum.CAMPAIGN_READY);
            //get the input files and process

            jobRepo.save(job);
        }
        return ResponseEntity.ok().build();
    }

    public Job updateJobStatus(Long jobId, String fileType) {
        System.out.println(jobId+" "+fileType);
        Optional<Job> job = jobRepo.findById(jobId);
        if (job.isPresent()) {
            if (job.get().getStatus().equals(JobEnum.NEW)) {
                if (fileType.equals("voterdata"))
                    job.get().setStatus(JobEnum.VOTER_DATA_UPLOADED);
                else if (fileType.equals("campaigncontent"))
                    job.get().setStatus(JobEnum.CANVAS_CONTENT_UPLOADED);
            } else {
                if (fileType.equals("voterdata") && job.get().getStatus().equals(JobEnum.CANVAS_CONTENT_UPLOADED)) {
                    job.get().setStatus(JobEnum.CAMPAIGN_READY);
                } else if (fileType.equals("campaigncontent") && job.get().getStatus().equals(JobEnum.VOTER_DATA_UPLOADED)) {
                    job.get().setStatus(JobEnum.CAMPAIGN_READY);
                }
            }
            return jobRepo.save(job.get());
        }
        return null;
    }

    public List<Job> fetchJobs() {
        return jobRepo.findAll();
    }
}
