drop table if exists sage.scrapperlog;
create table sage.scrapperlog (
   scrapperid long auto_increment not null primary key,
   mediaid int, 
   scantype int,
   searchtitle varchar,
   foundtitle varchar,
   foundkey varchar,
   scandate timestamp);
