drop index if exists sage.episode_idx;
drop index if exists sage.episode_idx2;
drop index if exists sage.series_idx;
drop table if exists sage.series;
drop index if exists sage.seriescast_idx;
drop table if exists sage.seriescast;
drop table if exists sage.seriesgenre_idx;
drop table if exists sage.seriesgenre;

create table sage.series (
  seriesid int not null primary key, 
  imdbid varchar,
  zap2itid varchar, 
  title varchar,
  firstair date,
  airday varchar,
  airtime varchar,
  status varchar,
  description varchar,
  network varchar,
  userrating float, 
  mpaarated varchar, 
  runtime bigint);
create table sage.seriescast ( 
  seriesid int,
  episodeid int,
  personid int,
  name varchar,
  job varchar,
  character varchar);
create table sage.seriesgenre (
  seriesid int,
  name varchar);
create table sage.episode (
  episodeid int not null,
  seriesid int,
  episodeno int,
  seasonid int,
  seasonno int,
  mediaid int,
  title varchar,
  description varchar,
  originalairdate date,
  userrating float,
  thumbpath varchar);

create index episode_idx on sage.episode(seriesid, episodeid);
create index episode_idx2 on sage.episode(episodeid);
create index episode_idx3 on sage.episode(mediaid);
create index sage.seriescast_idx on sage.seriescast(seriesid);
create index sage.seriesgenre_idx on sage.seriesgenre(seriesid);
