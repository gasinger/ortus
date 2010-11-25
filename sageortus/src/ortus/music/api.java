/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.music;

import java.util.ArrayList;
import java.util.List;
import ortus.music.MusicObject.Type;

/**
 *
 * @author jphipps
 */
public class api {
	public static boolean IsMusicArtist(MusicObject mo) {
		return mo.isArtist();
	}
	public static boolean IsMusicAlbum(MusicObject mo) {
		return mo.isAlbum();
	}
	public static boolean IsMusicTrack(MusicObject mo) {
		return mo.isTrack();
	}
	public static boolean IsMusicGenre(MusicObject mo) {
		return mo.isGenre();
	}	
	public static String GetMusicType(MusicObject mo) {
		return mo.getObjtype().name();
	}
	public static String GetMusicTitle(MusicObject mo) {
		return mo.getTitle();
	}
	public static int GetMusicAlbumCount(MusicObject mo) {
		return mo.getAlbumcount();
	}
	public static int GetMusicTrackCount(MusicObject mo) {
		return mo.getTrackcount();
	}
	public static long GetMusicDuration(MusicObject mo) {
		return mo.getDuration();
	}

	public static List<MusicObject> GetArtists() {
		String SQL = "SELECT artist, album, count(*), sum(m.mediaduration) FROM sage.music as sm, SAGE.MEDIA m where sm.mediaid = m.mediaid group by artist, album order by artist, album";
		List<List> result = ortus.api.executeSQLQueryArray(SQL);
		List<MusicObject> retval = new ArrayList<MusicObject>();

		MusicObject mo = null;
		String lastArtist = null;
		String lastAlbum = "";
		for ( List x : result) {
			if ( lastArtist == null) {
				lastArtist = (String)x.get(0);
				lastAlbum = (String)x.get(1);
				mo = new MusicObject();
				mo.setTitle(lastArtist);
			}
			if (  ! lastArtist.equalsIgnoreCase((String)x.get(0))) {
				if ( mo != null)
					retval.add(mo);
				mo = new MusicObject();
				mo.setTitle((String)x.get(0));
			}
			mo.addAlbumcount(1);
			mo.addTrackcount(Integer.parseInt((String)x.get(2)));
			mo.addDuration(Long.parseLong((String)x.get(3)));

			lastArtist = (String)x.get(0);
			lastAlbum = (String)x.get(1);
		}
		if ( mo != null)
			retval.add(mo);
		return retval;
	}
	public static List<MusicObject> GetAlbums() {
		String SQL = "SELECT album,count(*), sum(m.mediaduration) FROM sage.music as sm, SAGE.MEDIA m where sm.mediaid = m.mediaid group by album order by album";
		List<List> result = ortus.api.executeSQLQueryArray(SQL);
		List<MusicObject> retval = new ArrayList<MusicObject>();

		for ( List x : result) {
			retval.add(new MusicObject(Type.Album, (String)x.get(0), Integer.parseInt((String)x.get(1)), Long.parseLong((String)x.get(2))));
		}
		return retval;
	}
	public static List<MusicObject> GetTracks() {
		String SQL = "SELECT title,count(*), sum(m.mediaduration) FROM sage.music as sm, SAGE.MEDIA m where sm.mediaid = m.mediaid group by title order by title";
		List<List> result = ortus.api.executeSQLQueryArray(SQL);
		List<MusicObject> retval = new ArrayList<MusicObject>();

		for ( List x : result) {
			retval.add(new MusicObject(Type.Track, (String)x.get(0), Integer.parseInt((String)x.get(1)), Long.parseLong((String)x.get(2))));
		}
		return retval;
	}

	public static List<MusicObject> GetGenre() {
		String SQL = "SELECT g.name, count(*), sum(m.mediaduration) FROM sage.music as sm, SAGE.MEDIA m, sage.genre as g where sm.mediaid = g.mediaid and sm.mediaid = m.mediaid group by g.name order by g.name";
		List<List> result = ortus.api.executeSQLQueryArray(SQL);
		List<MusicObject> retval = new ArrayList<MusicObject>();

		for ( List x : result) {
			retval.add(new MusicObject(Type.Genre, (String)x.get(0), Integer.parseInt((String)x.get(1)), Long.parseLong((String)x.get(2))));
		}
		return retval;

	}

}
