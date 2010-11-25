drop table if exists sage.actor;
drop table if exists sage.actormovies;

create table sage.actor (
	id int primary key,
        name varchar,
        birthday varchar,
        birthplace varchar);

create table sage.actormovies (
 	id int,
        personid int,
	name varchar,
	character varchar,
	job varchar);
create index sage.actormovies_idx on sage.actormovies(personid);
