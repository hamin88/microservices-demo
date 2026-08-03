INSERT INTO rule_types (id, name)
VALUES (1,'FILEIMPORT');

INSERT INTO rule_types (id, name)
VALUES (2,'DATAFLOW');

INSERT INTO rules (id, name, rule_type_id)
VALUES (1,'MyFileImport', 1);

INSERT INTO rules (id, name, rule_type_id)
VALUES (2,'MyDataflow', 2);

INSERT INTO dataflows (rule_id)
VALUES (2);

INSERT INTO scheduled_jobs (job_id, rule_id, cron_expression, active)
VALUES ('fileimport_job', 1 , '0 0/1 * * * ?', true);

INSERT INTO scheduled_jobs (job_id, rule_id, cron_expression, active)
VALUES ('dataflow_job', 2 , '0 0/2 * * * ?', true);

