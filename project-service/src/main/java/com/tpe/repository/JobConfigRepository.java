package com.tpe.repository;

import com.tpe.model.JobConfig;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobConfigRepository extends JpaRepository<JobConfig, Long> {
    List<JobConfig> findByActiveTrue();
}
