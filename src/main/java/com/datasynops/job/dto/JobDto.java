package com.datasynops.job.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.datasynops.job.entity.JobEnum;
import com.datasynops.job.entity.JobTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@AllArgsConstructor

@Data
public class JobDto {
    private String jobName;
    private JobEnum status;
    private JobTypeEnum jobType;
    private Timestamp startedAt;
    private String createdBy;
    private String optionalTextString;
    private long campaignGeneratedCount;
    private long canvasCompletedCount;
    private long canvasSubmittedCount;
    private long totalCount;

    public JobDto() {
    }
}
