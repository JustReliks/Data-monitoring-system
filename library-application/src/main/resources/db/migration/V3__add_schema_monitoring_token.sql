CREATE TABLE lib.t_monitoring_token
(
    id         serial primary key,
    token      varchar   not null unique,
    created_at timestamp not null
)