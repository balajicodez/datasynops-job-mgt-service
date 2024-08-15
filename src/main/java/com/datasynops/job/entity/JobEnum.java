package com.datasynops.job.entity;

public enum JobEnum {
    NEW,
    INIT,
    SCHEMA_VALIDATED,
    SCHEMA_VALIDATION_SUCCESFUL,
    DATA_UPLOADED,
    SCHEMA_GENERATED,
    RUNNING,
    RUN_COMPLETED,
    JOB_COMPLETED_FAILURE;

    JobEnum() {
    }
}
