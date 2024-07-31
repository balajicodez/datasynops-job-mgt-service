package com.datasynops.job.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.datasynops.job.entity.JobEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@AllArgsConstructor

@Data
public class JobDto {
    private String jobName;
    private JobEnum status;
    private Timestamp startedAt;
    private String createdBy;
    private String description;
   
    public JobDto() {
    }
}
