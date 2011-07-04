drop table if exists sage.mediavideos;
create table sage.mediavideos (
    mediaid int not null primary key, 
    mediayear char(6),
    description varchar,
    releasedate date,
    userrating int,
    mpaarated varchar, 
    metadatasource varchar,
    tmdbid int, 
    imdbid varchar,
    trailer varchar); 