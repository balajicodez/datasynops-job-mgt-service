package com.datasynops.job.repo;

import com.datasynops.job.entity.Job;
import com.datasynops.job.entity.JobDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JobRepo extends JpaRepository<Job, Long> {

    @Query(
            value = "SELECT campaign_generated_count, canvas_completed_count, canvas_submitted_count, id, started_at, total_count, created_by, job_name, job_type, optional_text, status, optional_graphic_content, content_path\n" +
                    "FROM public.job;",
            nativeQuery = true)
    List<Job> findAll();


}
