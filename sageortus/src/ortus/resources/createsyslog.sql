drop table if exists sage.syslog;
create table sage.syslog (
   syslogid long auto_increment not null primary key,
   event_level int,
   event_time timestamp, 
   event_msg varchar);
