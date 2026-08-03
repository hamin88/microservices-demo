CREATE TABLE rule_types (
id VARCHAR(255) PRIMARY KEY,
name VARCHAR(255) NOT NULL
);
CREATE TABLE rules (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    rule_type_id BIGINT, -- The foreign key column definition
    CONSTRAINT fk_rule_rule_type FOREIGN KEY (rule_type_id) REFERENCES rule_types(id)
    ON DELETE SET NULL -- Prevents crashes if a rule is deleted out of your system
    );

  CREATE TABLE dataflows (
      rule_id BIGINT PRIMARY KEY,
      CONSTRAINT fk_dataflow_rule
      FOREIGN KEY (rule_id) REFERENCES rules(id)
  );
-- Create your JobConfig entity table
CREATE TABLE scheduled_jobs (
    job_id VARCHAR(255) PRIMARY KEY,
    cron_expression VARCHAR(255) NOT NULL,
    rule_id BIGINT, -- The foreign key column definition
    active BOOLEAN NOT NULL,
    -- Enforce the database-level transactional link
      CONSTRAINT fk_job_config_rule FOREIGN KEY (rule_id) REFERENCES rules(id)
      ON DELETE SET NULL -- Prevents crashes if a rule is deleted out of your system
    );

-- (Optional) If you want Flyway to initialize Quartz tables instead of spring.quartz.jdbc.initialize-schema
-- You can copy the native Quartz H2 layout script here.
