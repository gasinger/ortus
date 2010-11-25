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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;

/**
 *
 * @author jphipps
 */
public class CastItem extends ortus.vars implements Serializable {

	String id = "";
	private String name;
	private String job;
	private String character;
	private String birthday;
	private String birthplace;
	List<HashMap> films = new ArrayList<HashMap>();
	List<HashMap> images = new ArrayList<HashMap>();

	public CastItem(String id, String name, String job, String character) {
		this.id = id;
		this.name = name;
		this.job = job;
		this.character = character;
//        ortus.api.DebugLog(TRACE2, " CastItem: " + name);
	}

	public String GetId() {
		return id;
	}

	public void SetId(String id) {
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
		if( id.isEmpty())
			return;
                Connection conn = ortus.api.GetConnection();
                PreparedStatement ps = null;
                try {
                    ps = conn.prepareStatement("update sage.actor set name = ?, birthday=?, birthplace=? where id = ?");
                    ps.setString(1,name);
                    ps.setString(2,birthday);
                    ps.setString(3,birthplace);
                    ps.setString(4,id);
                    int updatecount = ps.executeUpdate();
                    ps.close();
                    if ( updatecount == 0) {
                        ps = conn.prepareStatement("insert into sage.actor ( id, name,birthday, birthplace) values(?,?,?,?)");
                        ps.setString(1,id);
                        ps.setString(2,name);
                        ps.setString(3,birthday);
                        ps.setString(4,birthplace);
                        ps.executeUpdate();
                        ps.close();
                    }

                    for ( HashMap film : films) {
                        ps = conn.prepareStatement("update sage.actormovies set name = ?, character=?, job=? where id = ? and personid = ?");
                        ps.setString(1,name);
                        ps.setString(2,character);
                        ps.setString(3,job);
                        ps.setInt(4,Integer.parseInt((String)film.get("id")));
                        ps.setInt(5,Integer.parseInt(id));

                        updatecount = ps.executeUpdate();
                        ps.close();
                        if ( updatecount == 0 ) {
                            ps = conn.prepareStatement("insert into sage.actormovies (id, personid, name, character,job) values(?,?,?,?,?)");
                            ps.setInt(1,Integer.parseInt(id));
                            ps.setInt(2,Integer.parseInt((String)film.get("id")));
                            ps.setString(3,name);
                            ps.setString(4,character);
                            ps.setString(5,job);
                            ps.close();
                        }
                    }
                    for ( HashMap image : images) {
                            ImageItem ii = new ImageItem("CA"+id,name.replaceAll("'","''"),"",(String)image.get("url"));
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
		if ( id.isEmpty())
			return;
		for ( HashMap image : images) {
			ImageItem ii = new ImageItem("CA"+id,name.replaceAll("'","''"),"",(String)image.get("url"));
			ii.getImage(dest);

		}
	}
}
