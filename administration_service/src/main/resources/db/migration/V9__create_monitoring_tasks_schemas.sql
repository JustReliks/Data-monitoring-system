CREATE TABLE dms.t_monitoring_task_config
(
    id         SERIAL primary key,
    schema     varchar                               not null,
    name       varchar                               not null,
    project_id integer references dms.t_project (id) not null,
    topic_id   integer references dms.t_topic (id)   not null
);

CREATE TABLE dms.t_monitoring_task_instance
(
    id        SERIAL primary key,
    config_id integer references dms.t_archive_task_config (id) not null,
    status    varchar                                           not null
)
