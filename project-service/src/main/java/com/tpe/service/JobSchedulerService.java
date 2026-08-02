package com.tpe.service;

import org.quartz.*;
import org.springframework.stereotype.Service;
import com.tpe.model.JobConfig;

@Service
public class JobSchedulerService {

    private final Scheduler scheduler;
    // JobTaskFactory remains unchanged as Quartz can reference it during job execution

    public JobSchedulerService(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void scheduleDatabaseJob(JobConfig jobConfig) {
        String jobId = jobConfig.getJobId();

        // 1. Preserve logic: If the job is marked inactive, remove it from the runtime scheduler
        if (!jobConfig.isActive()) {
            cancelJob(jobId);
            return;
        }

        try {
            // 2. Preserve logic: Validate cron and setup keys
            JobKey jobKey = new JobKey("job-" + jobId, "database-group");
            TriggerKey triggerKey = new TriggerKey("trigger-" + jobId, "database-group");

            // 3. Preserve logic: Resolve the executable task logic at execution time by saving metadata
            JobDetail jobDetail = JobBuilder.newJob(DataflowJob.class)
                    .withIdentity(jobKey)
                    .usingJobData("jobType", jobConfig.getJobType()) // Passed to factory later
                    .usingJobData("jobId", jobId)
                    .build();

            // Translate your CronTrigger logic into Quartz format
            Trigger cronTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .withSchedule(CronScheduleBuilder.cronSchedule(jobConfig.getCronExpression()) //  Correct method name
                            .withMisfireHandlingInstructionDoNothing())
                    .build();

            // 4. Preserve logic: Thread-safe replacement (Quartz overwrites safely in H2 database)
            if (scheduler.checkExists(jobKey)) {
                // If it already exists, unschedule and re-schedule to update the cron/logic
                scheduler.deleteJob(jobKey);
            }

            scheduler.scheduleJob(jobDetail, cronTrigger);

        } catch (SchedulerException e) {
            throw new RuntimeException("Failed to dynamically update Quartz job: " + jobId, e);
        }
    }

    // Updated cancelJob method using Quartz
    public void cancelJob(String jobId) {
        try {
            JobKey jobKey = new JobKey("job-" + jobId, "database-group");
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
            }
        } catch (SchedulerException e) {
            throw new RuntimeException("Failed to cancel Quartz job: " + jobId, e);
        }
    }
}
