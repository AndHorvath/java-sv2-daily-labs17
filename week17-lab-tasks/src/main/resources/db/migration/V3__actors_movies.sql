create table actors_movies (
    id bigint auto_increment,
    actor_id bigint,
    movie_id bigint,
    constraint pk_actors_movies primary key (id)
);