drop index if exists sage.fanart_idx;
drop index if exists sage.fanart_idx6;
drop table if exists sage.fanart;
create table sage.fanart (
   id int auto_increment,
   mediaid int,
   idtype varchar,
   default int default 0,
   type varchar,
   metadataid varchar,
   low_height int default 0,
   low_width int default 0,
   low_imagesize long default 0,
   low_url varchar,
   low_file varchar,
   medium_height int default 0,
   medium_width int default 0,
   medium_imagesize long default 0,
   medium_url varchar,
   medium_file varchar,
   high_height int default 0,
   high_width int default 0,
   high_imagesize long default 0,
   high_url varchar,
   high_file varchar);
create index sage.fanart_idx on sage.fanart(id,type);
create index sage.fanart_idx6 on sage.fanart(idtype, mediaid);
