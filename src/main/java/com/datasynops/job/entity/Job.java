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
    private String jobName;
    @Enumerated(EnumType.STRING)
    private JobEnum status;
    private Timestamp startedAt;
    private String createdBy;
    private String description;
    private String platform;
    private String dataFileName;
    public Job() {
    }
}
