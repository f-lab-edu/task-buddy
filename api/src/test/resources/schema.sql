create table users (
    id bigint auto_increment primary key,
    email varchar(100) unique not null,
    username varchar(100) unique not null,
    password varchar(200) not null,
    password_update_at timestamp not null,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp
);

create table tasks (
    id bigint auto_increment primary key,
    user_id bigint not null,
    title varchar(200) not null,
    is_done tinyint(1) not null default 0,
    description varchar(1500),
    start_date_time timestamp not null,
    end_date_time timestamp not null,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp,
    index idx_user_id (user_id)
);
