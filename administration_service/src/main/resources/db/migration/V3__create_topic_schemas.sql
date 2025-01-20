CREATE TABLE dms.t_topic
(

    id                 serial primary key,
    name               varchar not null,
    project_id         integer not null references dms.t_project (id),
    partitions         integer not null,
    replication_factor integer not null,
    uuid               varchar not null,
    name_in_kafka      varchar not null unique
)