drop table if exists sage.menu;
drop table if exists sage.menuitems;
drop table if exists sage.menuitemstatics;
create table sage.menu (
    userid int,
   menu Object);
insert into sage.tasks values('backupclean','Clean backup directory', '05:00:00', 86400000,'ortus.api.DeleteBackupFiles',null);
drop table if exists sage.episodemedia;
create table sage.episodemedia (
  mediaid int,
  episodeid int);
drop table if exists sage.customepisode;
create table sage.customepisode (
  episodeid int not null,
  seriesid int,
  episodeno int,
  seasonid int,
  seasonno int,
  mediaid int,
  title varchar,
  description varchar,
  originalairdate date,
  userrating float,
  thumbpath varchar);
insert into sage.episodemedia (select mediaid, episodeid from sage.episode where mediaid is not null and episodeid not = 999);
insert into sage.customepisode (select * from sage.episode where episodeid = 999);
delete from sage.episode where episodeid = 999;
alter table sage.episode drop column mediaid;
DROP VIEW IF EXISTS ALLMEDIA;
create view allmedia as
 select m.mediaid, v.name as showtitle, m.mediatitle as episodetitle, m.mediatype, m.mediagroup, m.airingstarttime, m.mediapath, m.mediaencoding, m.mediaimporttime,
      v.overview as description, v.certification as mpaarated, v.rating as userrating, v.releasedate as releasedate, m.mediaduration,m.mediasize,  0 as seriesid, 0 as seasonno, 0 as episodeid, 0 as episodeno
  from sage.media as m left join sage.metadata as v on m.mediaid=v.mediaid
 where m.mediatype in (0,1,2,4)
 union
select m.mediaid, m.mediatitle as showtitle, m.episodetitle as episodetitle, m.mediatype, m.mediagroup, m.airingstarttime, m.mediapath, m.mediaencoding, m.mediaimporttime,
      v.overview as description, v.certification as mpaarated, v.rating as userrating, v.releasedate as releasedate, m.mediaduration,m.mediasize,  0 as seriesid, 0 as seasonno, 0 as episodeid, 0 as episodeno
  from sage.media as m left join sage.metadata as v on m.mediaid=v.mediaid
 where m.mediatype in (5)
 union
select m.mediaid, v.artist as showtitle, v.album as episodetitle, m.mediatype, m.mediagroup, m.airingstarttime, m.mediapath, m.mediaencoding, m.mediaimporttime,
      v.title as description, '' as mpaarated, 0 as userrating, current_date as releasedate, m.mediaduration,m.mediasize,  0 as seriesid, 0 as seasonno, 0 as episodeid, v.trackno
  from sage.media as m left join sage.music as v on m.mediaid=v.mediaid
 where m.mediatype in (6)
 union
 select m.mediaid, s.title, e.title, m.mediatype, m.mediagroup, m.airingstarttime, m.mediapath, m.mediaencoding, m.mediaimporttime,
      e.description, s.mpaarated , s.userrating, e.originalairdate, m.mediaduration, m.mediasize, s.seriesid, e.seasonno, e.episodeid, e.episodeno
  from sage.media as m join sage.episodemedia as em on m.mediaid = em.mediaid,
          sage.series as s,
          sage.episode as e
 where m.mediatype = 3
    and em.episodeid = e.episodeid
    and e.seriesid = s.seriesid
union
 select m.mediaid, s.title, e.title, m.mediatype, m.mediagroup, m.airingstarttime, m.mediapath, m.mediaencoding, m.mediaimporttime,
      e.description, s.mpaarated , s.userrating, e.originalairdate, m.mediaduration, m.mediasize, s.seriesid, e.seasonno, e.episodeid, e.episodeno
  from sage.media as m join sage.customepisode as e on m.mediaid = e.mediaid,
          sage.series as s
 where m.mediatype = 3
    and e.seriesid = s.seriesid;