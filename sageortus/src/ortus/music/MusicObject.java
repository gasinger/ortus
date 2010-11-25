/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.music;

/**
 *
 * @author jphipps
 */
public class MusicObject {

	public enum Type {
		Artist,
		Album,
		Track,
		Genre
	}

	private String title = null;
	private Type objtype = null;
	private int albumcount = 0;
	private int trackcount = 0;
	private long duration = 0;

	public MusicObject() {
	}
	
	public MusicObject(Type objtype, String title, int count, long duration) {
		this.objtype = objtype;
		this.title = title;

		this.duration = duration;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the objtype
	 */
	public Type getObjtype() {
		return objtype;
	}

	/**
	 * @param objtype the objtype to set
	 */
	public void setObjtype(Type objtype) {
		this.objtype = objtype;
	}

	public boolean isArtist() {
		if( getObjtype() == Type.Artist)
			return true;
		else
			return false;
	}

	public boolean isAlbum() {
		if ( getObjtype() == Type.Album)
			return true;
		else
			return false;
	}

	public boolean isTrack() {
		if ( getObjtype() == Type.Track)
			return true;
		else
			return false;
	}

	public boolean isGenre() {
		if (getObjtype() == Type.Genre)
			return true;
		else
			return false;
	}

	/**
	 * @return the duration
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(long duration) {
		this.duration = duration;
	}

	public void addDuration(long duration) {
		this.duration=this.duration + duration;
	}
	/**
	 * @return the albumcount
	 */
	public int getAlbumcount() {
		return albumcount;
	}

	/**
	 * @param albumcount the albumcount to set
	 */
	public void setAlbumcount(int albumcount) {
		this.albumcount = albumcount;
	}

	public void addAlbumcount(int albumcount) {
		this.albumcount = this.albumcount + albumcount;
	}
	/**
	 * @return the trackcont
	 */
	public int getTrackcount() {
		return trackcount;
	}
	public void addTrackcount(int trackcount) {
		this.trackcount = this.trackcount + trackcount;
	}
}
