/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.onlinescrapper.themoviedb;

import java.io.Serializable;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import sagex.api.MediaFileAPI;

/**
 *
 * @author jphipps
 */
public class ImageItem extends ortus.vars implements Serializable {

    private String metadataid;
    private String type;
    private String url = "";
    private String size;
    private int width = 0;
    private int height = 0;
    private int imagetype = 0;

    public ImageItem() {
    }

    public void SetType(String type) {
        this.type = type.replace("backdrop","Backgrounds");
        this.type = this.type.replace("poster","Posters");
    }
    public void SetSize(String size) {
	    this.size=size;
    }
    public void SetUrl(String url) {
	    this.url = url;
    }

    public void setMetadataid(String metadataid) {
        this.metadataid = metadataid;
    }

    public String getMetadataid() {
        return metadataid;
    }

    public ImageItem(String metadataid, String type, String size, String url) {
        this.metadataid = metadataid;
        this.type = type.replace("backdrop","Backgrounds");
        this.type = this.type.replace("poster","Posters");
        this.size = size;
        this.url = url;
//        ortus.api.DebugLog(LogLevel.Trace2, " ImageItem: " + type + " url: " + url);
    }

    public boolean IsPoster() {
        if( type.equals("Posters"))
            return true;
        else
            return false;
    }

    public boolean IsBackgrounds() {
        if( type.equals("Backgrounds"))
            return true;
        else
            return false;
    }

    public boolean IsBanner() {
        if ( type.equals("Banners"))
            return true;
        else
            return false;
    }

    public boolean IsOriginal() {
        if ( size.equalsIgnoreCase("original"))
            return true;
        else
            return false;
    }
    public String GetUrl() {
        return url;
    }

    public void WriteImageDB() {

       Connection conn = ortus.api.GetConnection();

       try {
           QueryRunner qr = new QueryRunner();

//           int updatecount = qr.update(conn,"update sage.fanart set type = ?, url = ? where metadataid = ?",type, url,metadataid);
//           if ( updatecount == 0)
             int updatecount = qr.update(conn,"insert into sage.fanart (metadataid, type, url) values ( ?,?,?)", metadataid, type,url);
       } catch ( Exception e) {
           ortus.api.DebugLog(LogLevel.Error,"ImageItem: Exception: " + e);
           e.printStackTrace();
       } finally {
           try { DbUtils.close(conn); } catch ( Exception e) {}
       }
    }

//	public void WriteFileImageDB(Object mediafile) {
////		ortus.api.DebugLog(LogLevel.Trace2,"WriteFileImagetoDB: Writing: " + url);
//		String fafilename = url.substring(url.lastIndexOf(java.io.File.separator) + 1);
//
//		HashMap<String, String> imginfo = ortus.image.util.GetImageInfo(ortus.api.GetFanartFolder() + java.io.File.separator + url);
////		ortus.api.DebugLog(LogLevel.Trace2,"WriteFileImagetoDB: Completed imginfo: " + url);
//		if (imginfo != null) {
//			try {
//				width = Integer.parseInt(imginfo.get("width"));
//				height = Integer.parseInt(imginfo.get("height"));
//			} catch ( Exception e) {}
//
//			if (height < 200) {
//				imagetype = 1;
//			}
//			if (height < 600) {
//				imagetype = 2;
//			}
//			if (height > 599) {
//				imagetype = 3;
//			}
//
////			ortus.api.DebugLog(LogLevel.Trace, "FanartImage: ImageType: " + imagetype + " Size: Width: " + width + " Height: " + height);
//		}
//
////		ortus.api.DebugLog(LogLevel.Trace2,"WriteFileImagetoDB: Completed imginfo: " + url);
//		List<Object> result = ortus.api.executeSQLQuery("select mediaid from sage.fanart where mediaid = " + MediaFileAPI.GetMediaFileID(mediafile) + " and url like '%/" + fafilename + "'");
//		if (result.size() > 0) {
//			String SQL = "update sage.fanart set width = " + width + ", height="+height + ",imagetype = " + imagetype + ", file='" + url + "' where mediaid = " + MediaFileAPI.GetMediaFileID(mediafile) + " and url like '%/" + fafilename + "'";
//			ortus.api.executeSQL(SQL);
//		} else {
//			String SQL = "INSERT INTO sage.fanart (mediaid, width, height, imagetype, type, url, file) VALUES(" + MediaFileAPI.GetMediaFileID(mediafile) + ","+width+","+height+","+imagetype+",'" + type + "','" + url + "','" + url + "')";
//			ortus.api.executeSQL(SQL);
//		}
//	}

    public void getImage(String destination) {

        String downloaddir = ortus.api.GetFanartFolder() + java.io.File.separator + destination + java.io.File.separator + type;
        String filename = url.substring(url.lastIndexOf("/") + 1);

	filename = filename.replaceAll("'","''");
//        ortus.api.DebugLog(LogLevel.Trace2, " getImage: " + filename + " dir: " + downloaddir);

 //       File df = new File(downloaddir);
 //       if ( ! df.exists())
 //           df.mkdirs();

//        downloaddir+= java.io.File.separator + filename;
        if ( ortus.onlinescrapper.tools.urldownload.fileDownload(url, downloaddir ).equals("OK") ) {
            HashMap<String,String> imginfo = ortus.image.util.GetImageInfo(downloaddir + java.io.File.separator + filename);

            if ( imginfo != null) {
                    width = Integer.parseInt(imginfo.get("width"));
                    height = Integer.parseInt(imginfo.get("height"));

                    if ( height < 200)
                            imagetype = 1;
                    if ( height < 600)
                            imagetype = 2;
                    if ( height > 599)
                            imagetype = 3;

                    ortus.api.DebugLog(LogLevel.Trace,"FanartImage: ImageType: " + imagetype + " Size: Width: " + width + " Height: " + height);
            }

            String SQL ="UPDATE sage.fanart SET width = " + width + ", height="+height + ",imagetype = " + imagetype + ",file = '"+ destination.replaceAll("'","''") + java.io.File.separator + type.replaceAll("'","''") + java.io.File.separator + filename + "' WHERE metadataid = '" + metadataid + "' and type = '" + type.replaceAll("'","''") + "' and url = '" + url + "'";
            int success = ortus.api.executeSQL(SQL);
        }

        return ;
    }
    public void getImageFanart(Object mediafile, String destination) {

        String downloaddir = ortus.api.GetFanartFolder() + java.io.File.separator + destination;
        String filename = url.substring(url.lastIndexOf("/") + 1);

	filename = filename.replaceAll("'","''");
//        ortus.api.DebugLog(LogLevel.Trace2, " getImage: " + filename + " dir: " + downloaddir);

 //       File df = new File(downloaddir);
 //       if ( ! df.exists())
 //           df.mkdirs();

//        downloaddir+= java.io.File.separator + filename;
        if ( ortus.onlinescrapper.tools.urldownload.fileDownload(url, downloaddir ).equals("OK")) {
            HashMap<String,String> imginfo = ortus.image.util.GetImageInfo(downloaddir + java.io.File.separator + filename);

            if ( imginfo != null) {
                    width = Integer.parseInt(imginfo.get("width"));
                    height = Integer.parseInt(imginfo.get("height"));

                    if ( height < 200)
                            imagetype = 1;
                    if ( height < 600)
                            imagetype = 2;
                    if ( height > 599)
                            imagetype = 3;

                    ortus.api.DebugLog(LogLevel.Trace,"FanartImage: ImageType: " + imagetype + " Size: Width: " + width + " Height: " + height);
            }

            String SQL ="UPDATE sage.fanart SET width = " + width + ", height="+height + ",imagetype = " + imagetype + ",file = '"+ destination.replaceAll("'","''") + java.io.File.separator + filename + "' WHERE mediaid = 0 and type = '" + type.replaceAll("'","''") + "' and url = '" + url + "'";
            int success = ortus.api.executeSQL(SQL);
        }

        return ;
    }
}
