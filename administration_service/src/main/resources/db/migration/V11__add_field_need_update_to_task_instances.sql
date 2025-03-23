ALTER TABLE dms.t_archive_task_instance
    ADD COLUMN need_update bool default false not null;

ALTER TABLE dms.t_fulltext_task_instance
    ADD COLUMN need_update bool default false not null;

ALTER TABLE dms.t_monitoring_task_instance
    ADD COLUMN need_update bool default false not null;