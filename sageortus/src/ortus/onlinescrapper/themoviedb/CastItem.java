/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.onlinescrapper.themoviedb;

import java.io.File;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;

/**
 *
 * @author jphipps
 */
public class CastItem extends ortus.vars implements Serializable {
	private static final long serialVersionUID = 1L;
	
	int id = 0;
	private String name;
	private String job;
	private String character;
	private String birthday;
	private String birthplace;
        private String biography;
        private int known_movies;
	List<HashMap> films = new ArrayList<HashMap>();
	List<HashMap> images = new ArrayList<HashMap>();

	public CastItem(int id, String name, String job, String character) {
		this.id = id;
		this.name = name;
		this.job = job;
		this.character = character;
//        ortus.api.DebugLog(TRACE2, " CastItem: " + name);
	}

	public int GetId() {
		return id;
	}

	public void SetId(int id) {
		this.id = id;
	}

	public String GetName() {
		return name;
	}

	public String GetJob() {
		return job;
	}

	public String GetCharacter() {
		return character;
	}

//    public void getImage() {
//        String downloaddir = ortus.api.GetFanartFolder() + java.io.File.separator + "Cast" + java.io.File.separator + ortus.util.scrubString.ScrubFileName(name);
//        String filename = thumb.substring(thumb.lastIndexOf("/") + 1);
//
//        File af = new File(downloaddir + java.io.File.separator + filename);
//        if ( af.exists()) {
//            ortus.api.DebugLog(TRACE, "Cast: " + name + " already exists, skipping");
//            return;
//        }
//
//        ortus.api.DebugLog(TRACE2, " getImage: " + filename + " dir: " + downloaddir);
//        	ortus.onlinescrapper.tools.urldownload.fileDownload(thumb, downloaddir );
//
//        return;
//    }
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param job the job to set
	 */
	public void setJob(String job) {
		this.job = job;
	}

	/**
	 * @param character the character to set
	 */
	public void setCharacter(String character) {
		this.character = character;
	}

	/**
	 * @return the birthday
	 */
	public String getBirthday() {
		return birthday;
	}

	/**
	 * @param birthday the birthday to set
	 */
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	/**
	 * @return the birthplace
	 */
	public String getBirthplace() {
		return birthplace;
	}

	/**
	 * @param birthplace the birthplace to set
	 */
	public void setBirthplace(String birthplace) {
		this.birthplace = birthplace;
	}

	public List<HashMap> GetFilms() {
		return films;
	}

	public void AddFilms(HashMap f) {
		films.add(f);
	}

	public void AddImages(HashMap f) {
		images.add(f);
	}
	public void WriteToDB(Object mediafile) {
		if( id == 0)
			return;
                Connection conn = ortus.api.GetConnection();
                PreparedStatement ps = null;
                try {
                    ps = conn.prepareStatement("update sage.actor set name = ?, biography = ?, nomovies = ?, birthday=?, birthplace=? where id = ?");
                    ps.setString(1,name);
                    ps.setString(2,biography);
                    ps.setInt(3,known_movies);
                    ps.setString(4,birthday);
                    ps.setString(5,birthplace);
                    ps.setInt(6,id);
                    int updatecount = ps.executeUpdate();
                    ps.close();
                    if ( updatecount == 0) {
                        ps = conn.prepareStatement("insert into sage.actor ( id, name,biography, nomovies, birthday, birthplace) values(?,?,?,?,?,?)");
                        ps.setInt(1,id);
                        ps.setString(2,name);
                        ps.setString(3,biography);
                        ps.setInt(4,known_movies);
                        ps.setString(5,birthday);
                        ps.setString(6,birthplace);
                        ps.executeUpdate();
                        ps.close();
                    }

                    for ( HashMap film : films) {
                        ps = conn.prepareStatement("update sage.actormovies set name = ?, character=?, job=? where id = ? and personid = ?");
                        ps.setString(1,(String)film.get("name"));
                        ps.setString(2,(String)film.get("character"));
                        ps.setString(3,(String)film.get("job"));
                        ps.setInt(4,Integer.parseInt((String)film.get("id")));
                        ps.setInt(5,id);

                        updatecount = ps.executeUpdate();
                        ps.close();
                        if ( updatecount == 0 ) {
                            ps = conn.prepareStatement("insert into sage.actormovies (id, personid, name, character,job) values(?,?,?,?,?)");
                            ps.setInt(1,Integer.parseInt((String)film.get("id")));
                            ps.setInt(2,id);
                            ps.setString(3,(String)film.get("name"));
                            ps.setString(4,(String)film.get("character"));
                            ps.setString(5,(String)film.get("job"));
                            ps.executeUpdate();
                            ps.close();
                        }
                    }
                    for ( HashMap image : images) {
//                            ImageItem ii = new ImageItem(id, "CA",name.replaceAll("'","''"),"",(String)image.get("url"));
                            ImageItem ii = new ImageItem(id, "CA",(String)image.get("type"),(String)image.get("size"),(String)image.get("url"), (String)image.get("id"),0,0);
                            ii.WriteImageDB();
                    }

                } catch ( Exception e) {
                    ortus.api.DebugLogError("CastItem: WriteToDB: Exception",e);
                } finally {
                    try { DbUtils.close(ps); } catch (Exception e) {}
                    try { DbUtils.close(conn); } catch (SQLException ex) {}
                }
	}

	public void DownloadImages(Object mediafile, String dest) {
		if ( id == 0)
			return;
		for ( HashMap image : images) {
			ImageItem ii = new ImageItem(id,"CA",(String)image.get("type"),(String)image.get("size"),(String)image.get("url"), (String)image.get("id"),0,0);
			ii.getImage(dest);

		}
	}

    /**
     * @return the biography
     */
    public String getBiography() {
        return biography;
    }

    /**
     * @param biography the biography to set
     */
    public void setBiography(String biography) {
        this.biography = biography;
    }

    /**
     * @return the known_movies
     */
    public int getKnown_movies() {
        return known_movies;
    }

    /**
     * @param known_movies the known_movies to set
     */
    public void setKnown_movies(int known_movies) {
        this.known_movies = known_movies;
    }
}
