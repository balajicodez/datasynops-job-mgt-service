package com.datasynops.job.repo;

import com.datasynops.job.entity.Job;
import com.datasynops.job.entity.JobDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface JobDetailRepo extends JpaRepository<JobDetail, Long> {

    @Query(
            value = "SELECT * FROM JOB_DETAIL u WHERE u.STATUS = 'NEW' AND JOB_ID =?1 LIMIT 100",
            nativeQuery = true)
    List<JobDetail> findAllByStatusNewAndJobId(Long jobId);

    @Query(
            value = "SELECT * FROM JOB_DETAIL u WHERE u.STATUS = 'NEW' AND JOB_ID =?1 ",
            nativeQuery = true)
    List<JobDetail> findAllByStatusNewAndJobId(Long jobId, long limit);

    @Query(
            value = "SELECT * FROM JOB_DETAIL u WHERE ( u.STATUS = 'CANVASING_READY' OR u.STATUS = 'CANVASING_FAILED' ) AND JOB_ID =?1 LIMIT 1000",
            nativeQuery = true)
    List<JobDetail> findAllByStatusCanvasingReadyOrStatusCanvasingFailedAndJobId(Long jobId);

    @Query(
            value = "SELECT COUNT(*) FROM JOB_DETAIL u WHERE ( u.STATUS = 'CANVASING_READY' OR u.STATUS = 'CANVASING_FAILED' ) AND JOB_ID =?1 LIMIT 1000",
            nativeQuery = true)
    long findCountByStatusCanvasingReadyOrStatusCanvasingFailedAndJobId(Long jobId);

    @Query(
            value = "SELECT COUNT(*) FROM JOB_DETAIL u WHERE ( u.STATUS = 'CHANNEL_ASSIGNED' OR u.STATUS = 'CANVASING_SUBMITTED' OR u.STATUS = 'CANVASING_FAILED' ) AND JOB_ID =?1 LIMIT 1000",
            nativeQuery = true)
    long findCountByStatusChannelAssignedOrStatusCanvasingSubmittedOrStatusCanvasingFailedAndJobId(Long jobId);

    @Query(
            value = " SELECT * FROM JOB_DETAIL u WHERE ( u.STATUS = 'CANVASING_READY' OR u.STATUS = 'CANVASING_FAILED' ) AND JOB_ID =?1 ",
            nativeQuery = true)
    Page<JobDetail> findAllByStatusCanvasingReadyOrStatusCanvasingFailedAndJobId(Pageable pageable, Long jobId);


    @Query(
            value = "SELECT * FROM JOB_DETAIL u WHERE u.STATUS = 'CANVASING_SUBMITTED'  AND JOB_ID =?1 LIMIT 1000",
            nativeQuery = true)
    List<JobDetail> findAllByStatusCanvasingSubmittedAndJobId(Long jobId);
}
