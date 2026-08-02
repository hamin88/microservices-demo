INSERT INTO scheduled_jobs (job_id, job_type, cron_expression, active)
VALUES ('dataflow_job', 'DATAFLOW', '0 0/15 * * * ?', true);

INSERT INTO scheduled_jobs (job_id, job_type, cron_expression, active)
VALUES ('fileimport_job', 'FILE_IMPORT', '0 0 12 * * ?', true);
