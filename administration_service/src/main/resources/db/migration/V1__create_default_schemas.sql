create schema dms;

CREATE TABLE dms.t_user
(
    id       serial PRIMARY KEY,
    username varchar not null unique,
    password varchar not null
);

CREATE TABLE dms.t_project
(
    id               serial PRIMARY KEY,
    project_name     varchar not null unique,
    fulltext_quota   integer not null,
    archive_quota    integer not null,
    monitoring_quota integer not null
);

CREATE TABLE dms.t_user_project_permissions
(
    id         serial PRIMARY KEY,
    user_id    integer not null REFERENCES dms.t_user (id),
    project_id integer not null REFERENCES dms.t_project (id),
    permission varchar not null
);

CREATE TABLE dms.t_admin
(
    id       serial PRIMARY KEY,
    username varchar not null unique,
    token    varchar not null unique
);

INSERT INTO dms.t_admin(username, token)
VALUES ('initial_admin', 'aW5pdGlhbF9wYXNzd29yZA==');