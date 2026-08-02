package com.tpe.service;
import org.quartz.*;
import org.springframework.stereotype.Service;
import com.tpe.model.JobConfig;
@Service
public class QuartzSchedulerService {

    private final Scheduler scheduler;

    public QuartzSchedulerService(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * Dynamically registers or updates DataflowJob based on database configurations.
     */
    public void scheduleOrUpdateJob(JobConfig jobConfig) throws SchedulerException {
        JobKey jobKey = new JobKey(jobConfig.getJobId(), "dynamic-quartz-group");
        TriggerKey triggerKey = new TriggerKey(jobConfig.getJobId(), "dynamic-trigger-group");

        // 1. Handle job deactivation
        if (!jobConfig.isActive()) {
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
                System.out.println("Deleted Job from cluster: " + jobConfig.getJobId());
            }
            return;
        }

        // 2. Build Job Details targeting DataflowJob
        JobDetail jobDetail = JobBuilder.newJob(DataflowJob.class)
                .withIdentity(jobKey)
                .usingJobData("jobType", jobConfig.getJobType()) // Context payload
                .storeDurably() // Keeps the job structure even if triggers are replaced
                .build();

        // 3. Build the runtime Cron Trigger
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .withSchedule(CronScheduleBuilder.cronSchedule(jobConfig.getCronExpression()))
                .build();

        // 4. Update the live distributed scheduler state
        if (scheduler.checkExists(jobKey)) {
            // If the structure changed or cron changed, safely reschedule
            scheduler.rescheduleJob(triggerKey, trigger);
            System.out.println("Rescheduled existing QuartzJobBean: " + jobConfig.getJobId());
        } else {
            // First time registration
            scheduler.scheduleJob(jobDetail, trigger);
            System.out.println("Registered new QuartzJobBean: " + jobConfig.getJobId());
        }
    }
}
