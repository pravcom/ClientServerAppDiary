create table if not exists task
(
    id serial primary key,
    date_key date primary key,
    project varchar(50),
    description varchar(255),
    hours_spent numeric(5,2)
    );
