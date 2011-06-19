drop table if exists sage.properties;
drop table if exists sage.favoriteexcluder;
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
insert into sage.tasks values('backupclean','Clean backup directory', '05:00:00', 86400000,'ortus.api.DeleteBackupFiles',null);
insert into sage.tasks values('tvdbupdate','TVDB Update', '02:00:00', 86400000,'ortus.onlinescrapper.api.GetTVDBUpdates',null);
create table sage.properties
  ( userid int,
    key varchar,
    propval varchar);
drop table if exists sage.syslog;
create table sage.syslog (
   syslogid long auto_increment not null primary key,
   event_level int,
   event_time timestamp,
   event_msg varchar);
drop table if exists sage.metadatacache ;
create table sage.metadatacache (
    source varchar,
    id varchar,
    title varchar,
    match_title  varchar);