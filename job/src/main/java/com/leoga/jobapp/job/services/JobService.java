package com.leoga.jobapp.job.services;

import com.leoga.jobapp.job.dto.JobRequest;
import com.leoga.jobapp.job.dto.JobResponse;

import java.util.List;
import java.util.Optional;

public interface JobService {

    List<JobResponse> findAll();
    JobResponse createJob(JobRequest jobRequest);
    Optional<JobResponse> updateJob(Long id, JobRequest jobRequest);
    boolean deleteJobById(Long id);
    JobResponse getJobById(Long id);
}
