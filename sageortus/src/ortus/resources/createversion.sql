drop table if exists sage.version;
create table sage.version (
    db_version int,
    ortus_version int);
insert into sage.version ( db_version, ortus_version) values (3,1);