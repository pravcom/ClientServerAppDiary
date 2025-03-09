create table if not exists tasks
(
    id serial,
    date_key date,
    project varchar(50),
    description varchar(255),
    hours_spent numeric(5,2),
    primary key(id,date_key)
    );
