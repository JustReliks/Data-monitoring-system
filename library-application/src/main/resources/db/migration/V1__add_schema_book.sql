create schema lib;

CREATE TABLE lib.t_book
(
    id       serial PRIMARY KEY,
    author varchar not null,
    publisher varchar not null,
    published_date timestamp not null,
    price numeric not null
);

