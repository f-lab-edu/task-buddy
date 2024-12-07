create database if not exists taskbuddy_business;

use taskbuddy_business;

create table users (
    id bigint auto_increment primary key,
    email varchar(150) unique not null,
    username varchar(30) unique not null,
    password varchar(100) not null,
    password_updated_at timestamp not null default current_timestamp,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp
);

insert into users (email, username, password)
    values
        ('test_user1@gamil.com', 'test_user1', 'abcdabcd1234!@#$'),
        ('test_user2@gamil.com', 'test_user2', 'abcdabcd1234!@#$'),
        ('test_user3@gamil.com', 'test_user3', 'abcdabcd1234!@#$');

create table tasks (
    id bigint auto_increment primary key,
    user_id bigint not null,
    title varchar(50) not null,
    is_done tinyint(1) not null default 0,
    description varchar(200),
    start_date_time timestamp not null,
    end_date_time timestamp not null,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp
);

insert into tasks (user_id, title, is_done, description, start_date_time, end_date_time)
    values
        (1, 'task_title11', 1, 'task_test_description', current_timestamp, date_add(current_timestamp, interval 1 day)),
        (1, 'task_title12', 0, 'task_test_description', current_timestamp, date_add(current_timestamp, interval 2 day)),
        (1, 'task_title13', 0, 'task_test_description', current_timestamp, date_add(current_timestamp, interval 3 day)),
        (2, 'task_title21', 0, 'task_test_description', current_timestamp, date_add(current_timestamp, interval 1 day)),
        (2, 'task_title22', 1, 'task_test_description', current_timestamp, date_add(current_timestamp, interval 1 day)),
        (2, 'task_title23', 0, 'task_test_description', current_timestamp, date_add(current_timestamp, interval 2 day)),
        (3, 'task_title31', 1, 'task_test_description', current_timestamp, date_add(current_timestamp, interval 1 day)),
        (3, 'task_title32', 0, 'task_test_description', current_timestamp, date_add(current_timestamp, interval 1 day)),
        (3, 'task_title33', 0, 'task_test_description', current_timestamp, date_add(current_timestamp, interval 3 day)),
        (3, 'task_title34', 0, 'task_test_description', current_timestamp, date_add(current_timestamp, interval 3 day)),
        (3, 'task_title35', 0, 'task_test_description', current_timestamp, date_add(current_timestamp, interval 3 day));
