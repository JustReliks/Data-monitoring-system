ALTER TABLE dms.t_monitoring_task_config ADD COLUMN retention_time_sec integer not null default 604800;
ALTER TABLE dms.t_monitoring_task_config ADD COLUMN shard_group_duration_sec integer not null default 604800;
