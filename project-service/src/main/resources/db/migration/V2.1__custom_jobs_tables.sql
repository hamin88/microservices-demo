-- 1. Ensure the parent/target rules structure exists first
CREATE TABLE IF NOT EXISTS rules (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    rule_name VARCHAR(255) NOT NULL,
    rule_condition VARCHAR(1000)
);

-- Create your JobConfig entity table
CREATE TABLE scheduled_jobs (
    job_id VARCHAR(255) PRIMARY KEY,
    job_type VARCHAR(255) NOT NULL,
    cron_expression VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL,
    rule_id BIGINT, -- The foreign key column definition
    -- Enforce the database-level transactional link
      CONSTRAINT fk_job_config_rule FOREIGN KEY (rule_id) REFERENCES rules(id)
      ON DELETE SET NULL -- Prevents crashes if a rule is deleted out of your system
    );

-- (Optional) If you want Flyway to initialize Quartz tables instead of spring.quartz.jdbc.initialize-schema
-- You can copy the native Quartz H2 layout script here.
