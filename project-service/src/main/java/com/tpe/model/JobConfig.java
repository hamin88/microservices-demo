package com.tpe.model;
import jakarta.persistence.*;

@Entity
@Table(name = "scheduled_jobs")
public class JobConfig {
    
    @Id
    private String jobId;          // Unique key (e.g., "nightly_report")
    private String jobType;        // Type of task (e.g., "DATAFLOW", "FILEIMPORT")

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", referencedColumnName = "id", nullable = true)
    private Rule rule;

    private String cronExpression; // The dynamic cron string
    private boolean active;        // Flag to easily toggle the job

    // Getters, Setters, and Constructors
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }

    public Rule getRule() { return rule; }
    public void setRule(Rule rule) { this.rule = rule; }

    public String getCronExpression() { return cronExpression; }
    public void setCronExpression(String cronExpression) { this.cronExpression = cronExpression; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
