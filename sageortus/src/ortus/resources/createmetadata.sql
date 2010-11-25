drop table if exists sage.metadata;
create table sage.metadata (
    mediaid int not null primary key,
    tmdbid int,
    imdbid varchar,
    original_name varchar,
    name varchar(255),
    alternate_name varchar,
    url varchar,
    votes int,
    rating float,
    tagline varchar,
    certification varchar,
    releasedate date,
    runtime int,
    budget bigint,
    revenue bigint,
    homepage varchar,
    trailer varchar,
    overview varchar,
    metadatasource varchar);
drop index if exists sage.genere_idx;
drop table if exists sage.genre;

create table sage.genre (
   mediaid int,
   name varchar(50));
create index sage.genre_idx on sage.genre(mediaid);
drop index if exists sage.cast_idx;
drop table if exists sage.cast;
create table sage.cast (
   mediaid int,
   personid int,
   name varchar(255),
   job varchar(255),
   character varchar(255));
create index sage.cast_idx on sage.cast(mediaid);