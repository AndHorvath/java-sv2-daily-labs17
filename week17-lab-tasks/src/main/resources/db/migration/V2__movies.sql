CREATE table movies (
    id bigint auto_increment,
    title varchar(255),
    release_date date,
    constraint pk_movies primary key (id)
);