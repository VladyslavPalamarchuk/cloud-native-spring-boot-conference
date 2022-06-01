create table talk
(
    id          bigint not null constraint talk_pkey primary key,
    author      varchar(255),
    description varchar(255),
    name        varchar(255),
    type        integer
);

