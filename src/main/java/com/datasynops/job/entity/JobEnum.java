package com.datasynops.job.entity;

public enum JobEnum {
    NEW,
    INIT,
    SCHEMA_VALIDATED,
    SCHEMA_VALIDATION_SUCCESFUL,
    SURVEY_DATA_UPLOADED,
    JOB_RUNNING,
    JOB_COMPLETED_SUCCESSFUL,
    JOB_COMPLETED_FAILURE;

    JobEnum() {
    }
}
