ALTER TABLE dms.t_fulltext_task_config
    ADD COLUMN replication_factor int not null default 1;
ALTER TABLE dms.t_fulltext_task_config
    ADD COLUMN shards_count int not null default 1;