package com.tpe.controller;

import com.tpe.dto.TaskRequest;
import com.tpe.model.JobConfig;
import com.tpe.model.Rule;
import com.tpe.model.RuleType;
import com.tpe.service.JobSchedulerService;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jobconfig")
public class JobconfigController {

    private final JobSchedulerService jobSchedulerService;

    public JobconfigController(JobSchedulerService jobSchedulerService) {
        this.jobSchedulerService = jobSchedulerService;
    }

    @GetMapping("/schedule")
    public ResponseEntity<String> scheduleAtTimestamp() {
        //@RequestBody TaskRequest request
        try {
            String rawJobId = "dataflow_job_attime";
            String rawJobType = "DATAFLOW";
            Long rawRuleId = 45L;
            String rawTimeStr = "2026-08-25T15:30:00"; // ISO-8601 pattern string
            TaskRequest request = new TaskRequest(rawJobId, rawJobType, rawRuleId, rawTimeStr);
            JobConfig jobConfig = new JobConfig();
            jobConfig.setJobId(rawJobId);
            Rule rule = new Rule();
            rule.setId(2L);
            RuleType ruleType = new RuleType();
            ruleType.setId(2L);
            ruleType.setName("DATAFLOW");
            rule.setRuleType(ruleType);
            jobConfig.setRule(rule);
            jobConfig.setActive(true);
            jobSchedulerService.scheduleOrUpdateJob(jobConfig , false, request.getQuartzExecutionDate());
            return ResponseEntity.ok("Persistent task successfully registered into H2 database context.");
        } catch (SchedulerException e) {
            return ResponseEntity.internalServerError().body("Quartz engine ingestion failure: " + e.getMessage());
        }
    }
}
