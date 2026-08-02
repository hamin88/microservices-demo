package com.tpe.service;
import org.springframework.scheduling.quartz.*;
import org.quartz.*; // Imports TriggerBuilder, CronScheduleBuilder, JobBuilder, etc.

import org.springframework.stereotype.Service;
import com.tpe.model.JobConfig;
import java.util.* ;

@Service
public class JobSchedulerService {

    private final Scheduler scheduler;

    private final Map<String, Class<? extends QuartzJobBean>> jobClassMap = Map.of(
            "FILEIMPORT", FileImportJob.class,
            "DATAFLOW", DataflowJob.class
    );
    public JobSchedulerService(Scheduler scheduler) {
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
        Long ruleId = Optional.ofNullable(jobConfig)
                .map(config -> config.getRule())       // Safely extracts Rule (skips if config is null)
                .map(rule -> rule.getId())       // Safely extracts RuleType (skips if rule is null)
                .orElse(null);

        String jobType =  Optional.ofNullable(jobConfig)
                .map(config -> config.getRule())
                .map(rule -> rule.getRuleType()) // Assuming your Rule entity has a getRuleType() method
                .map(ruleType -> ruleType.getName()) // Assuming your RuleType enum/entity has a getName() method
                .map(String::toUpperCase) // Converts to uppercase safely if name exists
                .orElse("DEFAULT");

        Class<? extends QuartzJobBean> jobClass = jobClassMap.get(jobType.toUpperCase());
        if (ruleId == null || jobClass == null) {
            throw new IllegalArgumentException("Unknown database job execution type: " + jobType);
        }

        JobDetail jobDetail = JobBuilder.newJob(DataflowJob.class)
                .withIdentity(jobKey)
                .usingJobData("ruleId", ruleId != null ? ruleId.toString() : "")
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
