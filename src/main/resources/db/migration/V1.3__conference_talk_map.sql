create table conference_talk_map
(
    conference_id bigint not null references conference,
    talk_id       bigint not null constraint uk_unique_task_id unique references talk
);
