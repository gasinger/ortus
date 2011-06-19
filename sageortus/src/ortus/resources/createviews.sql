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
DROP VIEW IF EXISTS ALLMEDIAGENRE;
create view allmediagenre as
select  m.mediaid,
        m.mediatitle as showtitle,
        m.episodetitle,
        m.mediapath,
        m.mediaencoding,
        m.mediatype,
        m.mediagroup,
        m.mediaseclvl,
        m.mediasize,
        m.mediaduration,
        m.lastwatchedtime,
        m.airingstarttime,
        m.mediaimporttime,
        v.releasedate as releasedate,
        v.certification as mpaarated,
        g.name
  from sage.media as m,
       sage.genre as g,
       sage.metadata as v
 where m.mediaid = g.mediaid
   and m.mediaid = v.mediaid
union
select m.mediaid,
        m.mediatitle as showtitle,
        m.episodetitle,
        m.mediapath,
        m.mediaencoding,
        m.mediatype,
        m.mediagroup,
        m.mediaseclvl,
        m.mediasize,
        m.mediaduration,
        m.lastwatchedtime,
        m.airingstarttime,
        m.mediaimporttime,
        e.originalairdate,
        s.mpaarated,
        sg.name
  from sage.media as m,
       sage.episode as e,
       sage.seriesgenre as sg,
       sage.series as s
 where m.mediaid = e.mediaid
   and e.seriesid = sg.seriesid
   and e.seriesid = s.seriesid;
DROP VIEW IF EXISTS ALLCAST;
create view allcast as
select c.mediaid, c.name, c.job, c.character
  from sage.cast as c
union
select e.mediaid , c.name, c.job , c.character
  from sage.seriescast as c,
       sage.episode as e
where e.seriesid = c.seriesid
  and e.mediaid is not null;
drop view if exists allepisode;
create view allepisode as select s.seriesid,e.episodeid, em.mediaid, s.title as showtitle, e.title as episodetitle, s.firstair, s.airday, s.airtime, s.status, s.network, s.userrating, s.mpaarated, s.runtime, e.seasonno, e.episodeno, e.originalairdate from (select t.seriesid, t.seasonno, max(e.episodeno) as episodeno from (select distinct(e.seriesid), max(e.seasonno) as seasonno from sage.episode as e, sage.episodemedia as em where e.episodeid = em.episodeid group by seriesid order by 1,2) as t, sage.episode as e,sage.episodemedia as em where t.seriesid = e.seriesid and t.seasonno = e.seasonno and e.episodeid = em.episodeid group by t.seriesid, t.seasonno order by 1,2,3) as t, sage.episode as e, sage.series as s,sage.episodemedia as em where t.episodeno = e.episodeno and t.seriesid = e.seriesid and t.seasonno = e.seasonno and s.seriesid = e.seriesid and e.episodeid = em.episodeid;
