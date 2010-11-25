drop table if exists sage.music;

create table sage.music (
  mediaid int not null primary key,
  artist varchar,
  album varchar,
  trackno int,
  title varchar);

