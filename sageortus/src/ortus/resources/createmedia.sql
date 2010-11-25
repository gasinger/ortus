drop index if exists sage.media_idx;
drop index if exists sage.media_idx2;
drop index if exists sage.media_idx3;
drop table if exists sage.media;

create table sage.media (
   mediaid int not null primary key,
   mediatitle varchar,
   episodetitle varchar,
   mediapath varchar,
   mediaencoding varchar, 
   mediatype int,
   mediagroup int,
   mediaseclvl int default 0,
   mediasize long,
   mediaduration long,
   lastwatchedtime long,
   airingstarttime long,
   mediaimporttime timestamp);
create index sage.media_idx on sage.media(mediatype, mediagroup);
create index sage.media_idx2 on sage.media(airingstarttime);
create index sage.media_idx3 on sage.media(mediatitle);