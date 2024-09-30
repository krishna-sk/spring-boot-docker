package com.spring.boot.docker.repository;

import com.spring.boot.docker.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Integer> {
}
