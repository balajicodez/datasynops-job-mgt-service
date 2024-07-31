package com.datasynops.job.contoller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datasynops.job.dto.JobDto;
import com.datasynops.job.entity.Job;
import com.datasynops.job.service.JobService;


@CrossOrigin(value = {"*"}, exposedHeaders = {"Content-Disposition"})
@RestController
@RequestMapping("/jobs")
public class JobController {

    @Value("${spring.cloud.gcp.sql.enabled:false}")
    private boolean cloudDeployment;
    @Autowired
    JobService jobService;
   // @Autowired
    //ExcelProcessingService excelService;


    private String jobId;

    @GetMapping
    public List<Job> getJobs() {
        return jobService.fetchJobs();
    }

    @PutMapping
    public ResponseEntity<String> updateJob(@RequestBody Job job) throws Exception {
        System.out.println(" Job status " + job.getStatus());
       
        return ResponseEntity.ok(" ");
    }

    @PutMapping(value = "/msgstatus")
    public ResponseEntity<String> updateJobDetail(@RequestBody Job job) throws Exception {
        //String str = msgService.checkAndUpdateMsgDeliveryStatus(job);
        return ResponseEntity.ok("");
    }

    @PostMapping
    public Job createJob(@RequestBody JobDto jobDto) throws IOException, InvalidFormatException {
        Job job = jobService.createJob(jobDto);
        if(cloudDeployment) {
          //  processFilesFromCloudStorage(job);
        } else{
            processFilesFromLocal(job);
        }
        return job;
    }

    private void processFilesFromLocal(Job job) throws IOException, InvalidFormatException {
        File campaignDirectory = new File("C://project//"+String.valueOf(job.getJobName()));
        if(campaignDirectory.isDirectory() ){
            File[] files = campaignDirectory.listFiles();
            for(File f: files) {
                if (f.getName().contains(".xlsx")) {
                    //excelService.processExcelFile(f, job);
                }
            }
            for(File f: files) {
                if (f.getName().contains(".jpg")) {
                   // excelService.processFileAndCreateJobContent(f,String.valueOf(job.getId()));
                }
            }
        }
    }}



