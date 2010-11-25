drop index if exists sage.metadatacache_idx;
drop table if exists sage.metadatacache ;
drop table if exists sage.properties;
drop table if exists sage.favoriteexcluder;
create table sage.metadatacache
  ( imdbid  varchar,
    tmdbid  varchar,
    title   varchar,
    mediaobject other);
create index sage.metadatacache_idx on sage.metadatacache(imdbid, tmdbid);
drop table if exists sage.tasks;
create table sage.tasks
  ( taskid varchar,
    taskdescription varchar,
    tasktime time,
    taskinterval long,
    task varchar,
    taskparams Object);

insert into sage.tasks values('wizbackup','Sage Wiz.bin Backup Task', '03:00:00', 86400000, 'ortus.api.BackupWiz',null);
insert into sage.tasks values('dbbackup','Ortus H2 Database Backup Task', '04:00:00', 86400000,'ortus.api.BackupDatabase',null);
insert into sage.tasks values('tvdbupdate','TVDB Update', '02:00:00', 86400000,'ortus.onlinescrapper.api.GetTVDBUpdates',null);
create table sage.properties
  ( userid int,
    key varchar,
    propval varchar);