ALTER TABLE dms.t_monitoring_task_instance
    DROP CONSTRAINT t_monitoring_task_instance_config_id_fkey;

ALTER TABLE dms.t_monitoring_task_instance
    ADD CONSTRAINT t_monitoring_task_instance_config_id_fkey FOREIGN KEY (config_id) REFERENCES dms.t_monitoring_task_config (id);
