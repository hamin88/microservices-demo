package com.tpe.service;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import com.tpe.model.JobConfig;


@Service
public class JobSchedulerService {

    private final TaskScheduler taskScheduler;
    private final JobTaskFactory jobTaskFactory;
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    public JobSchedulerService(TaskScheduler taskScheduler, JobTaskFactory jobTaskFactory) {
        this.taskScheduler = taskScheduler;
        this.jobTaskFactory = jobTaskFactory;
    }

    /**
     * Call this whenever a job is created, updated, or enabled in the database.
     */
    public void scheduleDatabaseJob(JobConfig jobConfig) {
        String jobId = jobConfig.getJobId();

        // If the job is marked inactive, remove it from the runtime scheduler
        if (!jobConfig.isActive()) {
            cancelJob(jobId);
            return;
        }

        // Validate cron and resolve the executable task logic
        CronTrigger cronTrigger = new CronTrigger(jobConfig.getCronExpression());
        Runnable taskLogic = jobTaskFactory.getTask(jobConfig.getJobType());

        // Thread-safe scheduler replacement
        scheduledTasks.compute(jobId, (key, existingTask) -> {
            if (existingTask != null) {
                existingTask.cancel(true);
            }
            return taskScheduler.schedule(taskLogic, cronTrigger);
        });
        
        System.out.println("Successfully synchronized Job [" + jobId + "] from DB config.");
    }

    /**
     * Call this when a job is explicitly disabled or deleted.
     */
    public void cancelJob(String jobId) {
        ScheduledFuture<?> scheduledTask = scheduledTasks.remove(jobId);
        if (scheduledTask != null) {
            scheduledTask.cancel(true);
            System.out.println("Job [" + jobId + "] stopped and removed from memory.");
        }
    }
}
