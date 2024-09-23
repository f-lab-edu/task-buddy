create database taskbuddy default character set utf8mb4 collate utf8mb4_general_ci;

create table tasks (
    id bigint not null auto_increment primary key,
    title varchar(100) not null,
    is_done boolean not null default false,
    description varchar(300),
    start_date_time timestamp not null,
    end_date_time timestamp not null,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp
);
