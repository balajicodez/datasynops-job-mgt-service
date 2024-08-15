package com.datasynops.job.contoller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.datasynops.job.dto.JobDto;
import com.datasynops.job.entity.Job;
import com.datasynops.job.entity.JobEnum;
import com.datasynops.job.service.JobService;
import com.datasynops.job.service.S3Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@CrossOrigin(value = {"*"}, exposedHeaders = {"Content-Disposition"})
@RestController
@RequestMapping("/jobs")
public class JobController {

    @Value("${spring.cloud.gcp.sql.enabled:false}")
    private boolean cloudDeployment;

    @Autowired
    JobService jobService;

    @Autowired
    S3Service s3Service;
   // @Autowired
    //ExcelProcessingService excelService;

    private String jobId;

    @GetMapping
    public List<Job> getJobs() {
        return jobService.fetchJobs();
    }

    @GetMapping(value = "/{jobId}")
    public Job getJob(@PathVariable("jobId") Long jobId) {
        return jobService.fetchJob(jobId);
    }

    @PutMapping
    public ResponseEntity<String> updateJob(@RequestBody Job job) throws Exception {
        System.out.println(" Job status " + job.getStatus());
       
        return ResponseEntity.ok(" ");
    }

    @PutMapping(value = "/generate/{jobId}")
    public ResponseEntity<String> generateSchema(@PathVariable("jobId") Long jobId) throws Exception {
        jobService.generateSchema(jobId);
        return ResponseEntity.ok("");
    }

    @PutMapping(value = "/run/{jobId}")
    public ResponseEntity<String> runSchema(@PathVariable("jobId") Long jobId) throws Exception {
        jobService.runSchema(jobId);
        return ResponseEntity.ok("");
    }

    @PostMapping
    public Job createJob(@RequestBody JobDto jobDto) throws IOException, InvalidFormatException {
        Job job = jobService.createJob(jobDto);
        return job;
    }

    @PostMapping(value = "/file-uploads/{jobId}" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> handleFileUpload(@PathVariable("jobId") String path,  @RequestPart("file") Flux<FilePart> parts) throws IOException {
        //System.out.println("  files "+parts);
        return parts.flatMap(file -> {
            try {
                return saveFile(path, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Mono.empty();
        })
        .then(Mono.just("OK"))
        .onErrorResume(error -> Mono.just("Error uploading files "+error.getMessage()));


   /*       return  parts
     .filter(part -> part instanceof FilePart)
     .cast(FilePart.class)
     .flatMap(t -> {
        try {
            return saveFile(path, t);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Mono.empty();
    }).collectList()
     .then(Mono.just("File uploaded successfully")); */
    }

    private Mono<Void> saveFile(String path , FilePart filePart) throws Exception {
        String fileName = filePart.filename();
        System.out.println("  File Name "+fileName);
        Job job = jobService.fetchJob(Long.valueOf(path));
        s3Service.upload(job.getId()+"-"+job.getJobName() ,fileName, toInputStream(filePart.content()));
        System.out.println("  upload done ");
        job.setStatus(JobEnum.DATA_UPLOADED);
        jobService.updateJob(job);
        return Mono.empty();
    }

    

     private Mono<InputStream> toInputStream(Flux<DataBuffer> dataBufferFlux) {
        return DataBufferUtils.join(dataBufferFlux)
                .map(dataBuffer -> {
                    System.out.println(" databuffering... ");
                    InputStream targetStream = dataBuffer.asInputStream();
                    return targetStream;
                })
                .cache(); // Blocking to wait for the InputStream
    }
}



