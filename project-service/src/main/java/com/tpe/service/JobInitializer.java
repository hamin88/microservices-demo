package com.tpe.service;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import java.util.List;
import com.tpe.model.JobConfig;
import com.tpe.repository.JobConfigRepository;


@Component
public class JobInitializer {

    private final JobConfigRepository repository;
    private final JobSchedulerService schedulerService;

    public JobInitializer(JobConfigRepository repository, JobSchedulerService schedulerService) {
        this.repository = repository;
        this.schedulerService = schedulerService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeJobsOnStartup() {
        System.out.println("Loading active jobs from database...");
        List<JobConfig> activeJobs = repository.findByActiveTrue();
        
        for (JobConfig job : activeJobs) {
            try {
                schedulerService.scheduleDatabaseJob(job);
            } catch (Exception e) {
                System.err.println("Failed to schedule job " + job.getJobId() + " on startup: " + e.getMessage());
            }
        }
        System.out.println("Database job initialization complete.");
    }
}
