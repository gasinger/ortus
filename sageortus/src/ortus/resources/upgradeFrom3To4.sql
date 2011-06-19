alter table sage.metadata alter column rating int;
alter table sage.series alter column userrating int;
update sage.version set db_version = 4;