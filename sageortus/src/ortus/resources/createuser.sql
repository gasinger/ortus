drop table if exists sage.user;
drop table if exists sage.usermedia;
drop table if exists sage.userfavorites;

create table sage.user (
  userid int not null primary key,
  username varchar,
  userthumb varchar,
  userseclvl int default 0,
  userpin varchar);
create table sage.usermedia (
  userid int default 0,
  mediaid int default 0,
  watched boolean default false,
  lastwatchedtitle int default 0,
  lastwatchedtrack int default 0,
  lastwatchedtimestamp timestamp,
  lastwatchedtime bigint default 0);
create table sage.userfavorites (
  userid int,
  favorite varchar(100));
insert into sage.user ( userid, username) values( 0, 'Default');