drop index if exists sage.fanart_idx;
drop table if exists sage.fanart;
create table sage.fanart (
   mediaid int,
   default int default 0,
   height int default 0,
   width int default 0,
   imagetype int default 0,
   type varchar,
   url varchar,
   file varchar);
create index sage.fanart_idx on sage.fanart(mediaid,type);
