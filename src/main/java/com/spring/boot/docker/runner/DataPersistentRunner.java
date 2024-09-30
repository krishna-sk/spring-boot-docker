package com.spring.boot.docker.runner;

import com.spring.boot.docker.entity.Job;
import com.spring.boot.docker.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataPersistentRunner implements CommandLineRunner {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        System.out.println(dataSource.toString());
        List<Job> jobList = new ArrayList<>();
        jobList.add(Job.builder().profile("Software Engineer").experience("3 years").build());
        jobList.add(Job.builder().profile("Data Scientist").experience("5 years").build());
        jobList.add(Job.builder().profile("Product Manager").experience("4 years").build());
        jobList.add(Job.builder().profile("Web Developer").experience("2 years").build());
        jobList.add(Job.builder().profile("UX Designer").experience("3 years").build());
        jobList.add(Job.builder().profile("DevOps Engineer").experience("6 years").build());
        jobList.add(Job.builder().profile("Systems Analyst").experience("4 years").build());
        jobList.add(Job.builder().profile("QA Tester").experience("3 years").build());
        jobList.add(Job.builder().profile("Database Administrator").experience("5 years").build());
        jobList.add(Job.builder().profile("Network Engineer").experience("4 years").build());
        jobRepository.saveAll(jobList);
    }
}
