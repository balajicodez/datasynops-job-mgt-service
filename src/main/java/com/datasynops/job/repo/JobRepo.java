package com.datasynops.job.repo;

import com.datasynops.job.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JobRepo extends JpaRepository<Job, Long> {

    @Query(
            value = "SELECT id, job_name, description, status , platform, started_at, created_by, data_file_name \n" +
                    "FROM public.job;",
            nativeQuery = true)
    List<Job> findAll();

   
}
