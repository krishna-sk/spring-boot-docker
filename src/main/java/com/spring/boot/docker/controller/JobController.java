package com.spring.boot.docker.controller;

import com.spring.boot.docker.entity.Job;
import com.spring.boot.docker.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobs")
public class JobController {

    @Autowired
    private JobRepository jobRepository;

    @GetMapping("/{id}")
    public Job getJob(@PathVariable Integer id) {
        return jobRepository.findById(id).orElse(new Job(0, "job not found", "Please provide correctId"));
    }

    @GetMapping()
    public List<Job> getJobs() {
        return jobRepository.findAll();
    }

    @PostMapping("/create")
    public Job createJob(@RequestBody Job job) {
        return jobRepository.save(job);
    }
}
