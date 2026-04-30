package com.leoga.jobapp.job.services.impl;

import com.leoga.jobapp.job.clients.CompanyServiceClient;
import com.leoga.jobapp.job.clients.ReviewServiceClient;
import com.leoga.jobapp.job.dto.CompanyResponse;
import com.leoga.jobapp.job.dto.JobRequest;
import com.leoga.jobapp.job.dto.JobResponse;
import com.leoga.jobapp.job.model.Job;
import com.leoga.jobapp.job.repositories.JobRepository;
import com.leoga.jobapp.job.services.JobService;
import com.leoga.jobapp.job.mappers.JobMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final JobMapper jobMapper;
    private final CompanyServiceClient companyClient;
    private final ReviewServiceClient reviewClient;
    private int attempt = 0;

    @Override
    @RateLimiter(name = "rateBreaker", fallbackMethod = "rateBreakerFallback")
    public List<JobResponse> findAll() {
        return jobMapper.toJobResponseList(jobRepository.findAll(), companyClient, reviewClient);
    }


    @Override
    public JobResponse createJob(JobRequest jobRequest) {

        CompanyResponse company= companyClient.getCompanyById(jobRequest.getCompanyId());

        if (null == company) {
            return null;
        }

        Job savedJob = jobRepository.save(jobMapper.toEntity(jobRequest));
        return jobMapper.toJobResponse(savedJob, companyClient, reviewClient);
    }

    @Override
    @Retry(name = "retryBreaker", fallbackMethod = "retryFallback")
    public Optional<JobResponse> updateJob(Long id, JobRequest updatedJob) {
        System.out.println("Attempt: "+ ++attempt);
        return jobRepository.findById(id)
                .map(exixtingJob -> {
                    jobMapper.patchJob(updatedJob, exixtingJob);
                    Job savedJob = jobRepository.save(exixtingJob);
                    return jobMapper.toJobResponse(savedJob, companyClient, reviewClient);
                });
    }

    @Override
    public boolean deleteJobById(Long id) {
        return jobRepository.findById(id)
                .map(job -> {
                    jobRepository.deleteById(job.getId());
                    return true;
                }).orElse(false);
    }

    @Override
    @CircuitBreaker(name = "companyService", fallbackMethod = "circuitBreakerFallback")
    public JobResponse getJobById(Long id) {
        return jobMapper.toJobResponse(jobRepository.findById(id).orElse(null), companyClient, reviewClient);
    }

    public List<JobResponse> rateBreakerFallback(Exception e) {
        System.out.println("RATE BREAKER FALLBACK CALLED");
        return null;
    }

    public Optional<JobResponse> retryFallback(Long id, JobRequest updatedJob, Exception e) {
        System.out.println("RETRY FALLBACK CALLED");
        return Optional.empty();
    }

    public JobResponse circuitBreakerFallback(Long id, Exception e) {
        System.out.println("CIRCUIT BREAKER FALLBACK CALLED");
        return null;
    }
}
