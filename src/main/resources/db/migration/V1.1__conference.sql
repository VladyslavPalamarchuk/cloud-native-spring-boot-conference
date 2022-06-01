create table conference
(
    id                  bigint  not null constraint conference_pkey primary key,
    date                timestamp,
    name                varchar(255) constraint uk_unique_name unique,
    participants_number integer not null constraint conference_participants_number_check check (participants_number <= 100),
    subject             varchar(255)
);

