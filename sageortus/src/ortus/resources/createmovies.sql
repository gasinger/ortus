drop table if exists sage.movies;
create table sage.movies (
    metadataid varchar(15) not null primary key,
    mediaid int not null,
    tmdbid int,
    imdbid varchar,
    original_name varchar,
    name varchar(255),
    alternate_name varchar,
    url varchar,
    votes int,
    rating int,
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
   metadataid varchar,
   name varchar(50));
create index sage.genre_idx on sage.genre(metadataid);
drop index if exists sage.cast_idx;
drop table if exists sage.cast;
create table sage.cast (
   metadataid varchar,
   personid int,
   name varchar(255),
   job varchar(255),
   character varchar(255));
create index sage.cast_idx on sage.cast(metadataid);