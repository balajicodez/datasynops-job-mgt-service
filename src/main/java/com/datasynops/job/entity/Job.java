package com.datasynops.job.entity;


import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
@AllArgsConstructor
@Entity
@Data
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
   // @OneToMany(mappedBy="job",fetch = FetchType.LAZY)
   // private Set<JobDetail> jobDetail;
    private String jobName;
    @Enumerated(EnumType.STRING)
    private JobEnum status;
    @Enumerated(EnumType.STRING)
    private JobTypeEnum jobType;
    private Timestamp startedAt;
    private String createdBy;
    private byte[] optionalGraphicContent;

    @JsonIgnore
    private byte[] optionalText;

    @Transient
    private String optionalTextString;
    private long campaignGeneratedCount;
    private long canvasCompletedCount;
    private long canvasSubmittedCount;
    private long totalCount;
    private String contentPath;
    public Job() {
    }
}
