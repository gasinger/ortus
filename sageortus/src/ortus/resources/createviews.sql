DROP VIEW IF EXISTS ALLMEDIA;
create view allmedia as
 select m.mediaid, v.name as showtitle, m.mediatitle as episodetitle, m.mediatype, m.mediagroup, m.airingstarttime, m.mediapath, m.mediaencoding, m.mediaimporttime,
      v.overview as description, v.certification as mpaarated, v.rating as userrating, v.releasedate as releasedate, m.mediaduration,m.mediasize,  0 as seriesid, 0 as seasonno, 0 as episodeid, 0 as episodeno
  from sage.media as m left join sage.movies as v on m.mediaid=v.mediaid
 where m.mediatype in (0,1,2,4) and v.name is not null
 union
 select m.mediaid, m.mediatitle, m.mediatitle as episodetitle, m.mediatype, m.mediagroup, m.airingstarttime, m.mediapath, m.mediaencoding, m.mediaimporttime,
      'Not Available' as description, 'NA' as mpaarated,5 as userrating, '1970-01-01' as releasedate, m.mediaduration, m.mediasize, 0 as seriesid, 0 as seasonno, 0 as episodeid, 0 as episodeno
  from sage.media as m left join sage.movies as v on m.mediaid=v.mediaid
 where m.mediatype in (0,1,2,4) and v.name is null
 union
 select m.mediaid, s.title, e.title, m.mediatype, m.mediagroup, m.airingstarttime, m.mediapath, m.mediaencoding, m.mediaimporttime,
      e.description, s.mpaarated , s.userrating, e.originalairdate, m.mediaduration, m.mediasize, s.seriesid, e.seasonno, e.episodeid, e.episodeno
  from sage.media as m join sage.episode as e on m.mediaid = e.mediaid,
          sage.series as s
 where m.mediatype = 3
    and e.seriesid = s.seriesid;
DROP VIEW IF EXISTS ALLMEDIAGENRE;
create view allmediagenre as
select m.*, g.name
  from sage.media as m,
       sage.genre as g,
       sage.movies as v
 where m.mediaid = v.mediaid
   and v.metadataid = g.metadataid
union
select m.*, sg.name
  from sage.media as m,
       sage.episode as e,
       sage.seriesgenre as sg
 where m.mediaid = e.mediaid
   and e.seriesid = sg.seriesid;
DROP VIEW IF EXISTS ALLCAST;
create view allcast as
select v.mediaid, c.name, c.job, c.character
  from sage.cast as c,
       sage.movies as v
 where c.metadataid = v.metadataid
   and v.mediaid is not null
union
select e.mediaid , c.name, c.job , c.character
  from sage.seriescast as c,
       sage.episode as e
where e.seriesid = c.seriesid
  and e.mediaid is not null;
