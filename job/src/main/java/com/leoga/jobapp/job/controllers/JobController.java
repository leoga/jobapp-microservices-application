package com.leoga.jobapp.job.controllers;

import com.leoga.jobapp.job.dto.JobRequest;
import com.leoga.jobapp.job.dto.JobResponse;
import com.leoga.jobapp.job.services.JobService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/jobs")
public class JobController {

    private final JobService jobService;

    @GetMapping
    public ResponseEntity<List<JobResponse>> findAll() {
        return ResponseEntity.ok(jobService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJobById(@PathVariable Long id) {
        JobResponse job = jobService.getJobById(id);
        return job != null ? ResponseEntity.ok(job) :  ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<JobResponse> createJob(@RequestBody JobRequest jobRequest) {
        JobResponse job = jobService.createJob(jobRequest);
        return null != job ? new ResponseEntity<>(job, HttpStatus.CREATED) : ResponseEntity.badRequest().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<JobResponse> updateJob(@PathVariable Long id, @RequestBody JobRequest jobRequest) {
        return jobService.updateJob(id, jobRequest)
                .map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteJob(@PathVariable Long id) {
        return jobService.deleteJobById(id) ? ResponseEntity.ok("job deleted successfully") :  ResponseEntity.notFound().build();
    }
}